package com.group4.chatapp.services;

import com.group4.chatapp.dtos.attachment.AttachmentDto;
import com.group4.chatapp.dtos.attachment.PostAttachmentResponseDto;
import com.group4.chatapp.dtos.post.PostRequestDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.post.SharePostDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.mappers.UserMapper;
import com.group4.chatapp.models.*;
import com.group4.chatapp.models.Enum.*;
import com.group4.chatapp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.group4.chatapp.models.Enum.TargetType.ATTACHMENT;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class PostService {
    private AttachmentService attachmentService;
    private PostRepository postRepository;
    private UserService userService;
    private UserMapper userMapper;
    private InvitationRepository invitationRepository;
    private CloudinaryService cloudinaryService;
    private ContentRepository contentRepository;
    private NotificationService notificationService;

    private SimpMessagingTemplate simpMessagingTemplate;

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "Post isn't found!"
        ));
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(String username, int page) {
        User authUser = userService.getUserOrThrows();
        if (authUser.getUsername().equals(username)) {
            return getPostsByAuthUser(authUser, page);
        } else {
            return getPostsByUsername(username, page);
        }
    }

    public List<PostResponseDto> getPostsByAuthUser(User authUser, int page) {
        List<Post> posts = postRepository.getPostsByAuthUser(authUser, PageRequest.of(page-1, 20));

        return posts.stream().map(post -> {
            List<ReactionType> topReactionTypes = getTopReactionType(post.getId(), TargetType.POST);
            ReactionType reactionType = getUserReaction(post.getId(), TargetType.POST, post.getUser().getId());
            return new PostResponseDto(post, topReactionTypes, reactionType);
        }).toList();
    }

    public List<PostResponseDto> getPostsByUsername(String username, int page) {
        User authUser = userService.getUserOrThrows();
        User otherUser = userService.getUserByUsername(username);

        Invitation invitation = invitationRepository.findBySenderIdAndReceiverId(authUser.getId(), otherUser.getId());

        if (invitation.isBlock()) return new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page-1, 20);
        List<Post> posts;
        if (invitation.isAccepted()) {
            posts = postRepository.getPostsIfIsFriend(username, pageRequest);
        } else {
            posts = postRepository.getPostsIfIsNotFriend(username, pageRequest);
        }

        return posts.stream().map(post -> {
            List<ReactionType> topReactionTypes = getTopReactionType(post.getId(), TargetType.POST);
            ReactionType reactionType = getUserReaction(post.getId(), TargetType.POST, post.getUser().getId());
            return new PostResponseDto(post, topReactionTypes, reactionType);
        }).toList();
    }

    public PostResponseDto createPost(PostRequestDto dto)  {
        if (dto.getCaption() == null && dto.getAttachments() == null) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Caption and Attachments are not null!"
            );
        }
        User authUser = userService.getUserOrThrows();
        Post post = Post.builder()
                .caption(dto.getCaption())
                .captionBackground(dto.getCaptionBackground())
                .visibility(dto.getVisibility() != null ? dto.getVisibility() : PostVisibilityType.PUBLIC)
                .user(authUser)
                .build();
        List<Attachment> attachments = attachmentService.saveFilesWithDescription(dto.getAttachments());
        for (Attachment attachment : attachments) {
            attachment.setPost(post);
        }
        post.setAttachments(attachments);
        post.setStatus(Post.PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);

        List<User> friends = userService.getListFriend(authUser.getId()).stream().map(userMapper::toUser).toList();
        Notification notification = Notification.builder()
                .title("Bài viết mới từ bạn bè")
                .content(authUser.getUsername() + " đã đăng một bài viết mới.")
                .type(NotificationType.POST)
                .targetId(post.getId())
                .targetType(TargetType.POST)
                .build();
        notificationService.notifyAndCreateToUsers(authUser, friends, notification);

        return new PostResponseDto(post);
    }

    @Transactional
    public void updatePost(Long postId, PostRequestDto dto) {
        deletePost(postId);
        createPost(dto);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getPost(postId);
        List<Attachment> attachments = post.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            List<String> publicIds = new ArrayList<>();
            attachments.forEach(attachment -> {
                String publicId = cloudinaryService.getPublicIdByUrl(attachment.getSource());
                publicIds.add(publicId);
            });
            cloudinaryService.deleteMultiFile(publicIds);
        }
        if (post.getPostAttachmentType().equals(PostAttachmentType.POST)) {
            contentRepository.decreaseShares(post.getSharedPost().getId());
        }
        postRepository.deleteById(postId);
    }

    public PostResponseDto share(SharePostDto dto) {
        User authUser = userService.getUserOrThrows();
        Post post = getPost(dto.getPostId());
        Post newPost = Post.builder()
                .caption(dto.getCaption())
                .visibility(dto.getVisibility())
                .user(authUser)
                .build();
        if (dto.getType().equals(TargetType.POST)) {
            newPost.setPostAttachmentType(PostAttachmentType.POST);
            newPost.setSharedPost(post);
            contentRepository.increaseShares(dto.getPostId());
        } else if (dto.getType().equals(TargetType.ATTACHMENT)) {
            assert dto.getAttachmentId() != null;

            Attachment attachment = attachmentService.getAttachment(dto.getAttachmentId());

            newPost.setPostAttachmentType(PostAttachmentType.ATTACHMENT);
            newPost.setSharedAttachment(attachment);
            contentRepository.increaseShares(dto.getAttachmentId());
        } else {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Target type is not supported."
            );
        }

        return new PostResponseDto(postRepository.save(newPost));
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getNewsFeed(int page) {
        User authUser = userService.getUserOrThrows();
        List<UserWithAvatarDto> friends = userService.getListFriend(authUser.getId());
        List<Post> latestFriendPosts = new ArrayList<>();
        for (UserWithAvatarDto friend : friends) {
            Invitation invitation = invitationRepository.findBySenderIdAndReceiverId(authUser.getId(), friend.getId());
            if (!invitation.isBlock()) {
                List<Post> posts = postRepository.getNewPostByUserId(friend.getId(), PageRequest.of(page - 1, 1));
                if (posts != null && !posts.isEmpty() && posts.getFirst() != null) {
                    latestFriendPosts.add(posts.getFirst());
                }
            }
        }
        List<Post> topPostViews = postRepository.getPostsByTopViews(authUser.getId(), PageRequest.of(page - 1, 10));

        List<Post> newsFeeds = Stream.concat(
                latestFriendPosts.stream(),
                topPostViews.stream()
        ).toList();

        Set<Long> seenKeys = new HashSet<>();
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : newsFeeds) {
            Long key = post.getId();
            if (!seenKeys.contains(key)) {
                seenKeys.add(key);

                List<ReactionType> topReactionTypes = getTopReactionType(post.getId(), TargetType.POST);
                ReactionType reactionType = getUserReaction(post.getId(), TargetType.POST, post.getUser().getId());
                postResponseDtos.add(new PostResponseDto(post, topReactionTypes, reactionType));
            }
        }
        Collections.shuffle(postResponseDtos);
        return postResponseDtos;
    }

    public List<PostAttachmentResponseDto> getPostAttachments(Long postId) {
        Post post = postRepository.findPostAndAttachment(postId);
        List<PostAttachmentResponseDto> responses = new ArrayList<>();
        for (Attachment attachment : post.getAttachments()) {
            List<ReactionType> topReactionTypes = getTopReactionType(attachment.getId(), ATTACHMENT);
            ReactionType reactionType = getUserReaction(attachment.getId(), TargetType.ATTACHMENT, post.getUser().getId());

            responses.add(new PostAttachmentResponseDto(post.getId(), attachment, topReactionTypes, reactionType));
        }

        return responses;
    }

    public void increaseView(Long postId) {
        contentRepository.increaseViews(postId);
    }

    public List<ReactionType> getTopReactionType(Long postId, TargetType targetType) {
        return postRepository.getTopReactionType(postId, targetType, PageRequest.of(0, 3));
    }

    public ReactionType getUserReaction(Long postId, TargetType targetType, Long userId) {
        return postRepository.getUserReaction(postId, targetType, userId);
    }
}
