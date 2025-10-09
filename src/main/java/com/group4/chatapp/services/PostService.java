package com.group4.chatapp.services;

import com.group4.chatapp.dtos.post.PostRequestDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.post.SharePostDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.PostAttachmentType;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.User;
import com.group4.chatapp.models.UserRelation;
import com.group4.chatapp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class PostService {
    private AttachmentService attachmentService;
    private PostRepository postRepository;
    private UserService userService;
    private UserRelationRepository userRelationRepository;
    private ReactionService reactionService;
    private CloudinaryService cloudinaryService;
    private ContentRepository contentRepository;

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
            List<ReactionType> topReactionTypes = reactionService.getTopReactionType(post.getId());
            ReactionType reactionType = reactionService.getUserReaction(post.getId(), post.getUser().getId());
            return new PostResponseDto(post, topReactionTypes, reactionType);
        }).toList();
    }

    public List<PostResponseDto> getPostsByUsername(String username, int page) {
        User authUser = userService.getUserOrThrows();
        User otherUser = userService.getUserByUsername(username);
        UserRelation userRelation = userRelationRepository.getUserRelation(authUser.getId(), otherUser.getId());

        if (userRelation.getIsBlocking()) return new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page-1, 20);
        List<Post> posts;
        if (userRelation.isAccepted()) {
            posts = postRepository.getPostsIfIsFriend(username, pageRequest);
        } else {
            posts = postRepository.getPostsIfIsNotFriend(username, pageRequest);
        }

        return posts.stream().map(post -> {
            List<ReactionType> topReactionTypes = reactionService.getTopReactionType(post.getId());
            ReactionType reactionType = reactionService.getUserReaction(post.getId(), post.getUser().getId());
            return new PostResponseDto(post, topReactionTypes, reactionType);
        }).toList();
    }

    public void createPost(PostRequestDto dto)  {
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

        postRepository.save(post);
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

    public void sharePost(SharePostDto dto) {
        User authUser = userService.getUserOrThrows();
        Post post = getPost(dto.getSharedPostId());
        Post newPost = Post.builder()
                .caption(dto.getCaption())
                .visibility(dto.getVisibility())
                .sharedPost(post)
                .postAttachmentType(PostAttachmentType.POST)
                .user(authUser)
                .build();
        contentRepository.increaseShares(dto.getSharedPostId());
        postRepository.save(newPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getNewsFeed(int page) {
        User authUser = userService.getUserOrThrows();
        List<UserWithAvatarDto> friends = userService.getListFriend();
        List<Post> latestFriendPosts = new ArrayList<>();
        for (UserWithAvatarDto friend : friends) {
            UserRelation userRelation = userRelationRepository.getUserRelation(authUser.getId(), friend.getId());
            if (userRelation.getIsBlocking()) {
                List<Post> posts = postRepository.getNewPostByUserId(friend.getId(), PageRequest.of(page - 1, 1));
                if (posts != null && !posts.isEmpty() && posts.getFirst() != null) {
                    latestFriendPosts.add(posts.getFirst());
                }
            }
        }
        List<Post> topPostViews = postRepository.getPostsByTopViews(PageRequest.of(page - 1, 10));

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

                List<ReactionType> topReactionTypes = reactionService.getTopReactionType(post.getId());
                ReactionType reactionType = reactionService.getUserReaction(post.getId(), post.getUser().getId());
                postResponseDtos.add(new PostResponseDto(post, topReactionTypes, reactionType));
            }
        }
        Collections.shuffle(postResponseDtos);
        return postResponseDtos;
    }
}
