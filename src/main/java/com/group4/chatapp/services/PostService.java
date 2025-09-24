package com.group4.chatapp.services;

import com.group4.chatapp.dtos.UpdateFileDto;
import com.group4.chatapp.dtos.UploadFileDto;
import com.group4.chatapp.dtos.post.PostRequestDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.post.SharePostDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.mappers.PostMapper;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.PostAttachmentType;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class PostService {
    private AttachmentService attachmentService;
    private PostRepository postRepository;
    private UserService userService;
    private InvitationRepository invitationRepository;
    private ReactionService reactionService;
    private PostMapper postMapper;
    private CloudinaryService cloudinaryService;

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "Post isn't found!"
        ));
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUser(String username, int page) {
        User authUser = userService.getUserOrThrows();
        if (authUser.getUsername().equals(username)) {
            return getPostsByAuthUser(authUser, page);
        } else {
            return getPostsByUsername(username, page);
        }
    }

    public List<PostResponseDto> getPostsByAuthUser(User authUser, int page) {
        List<Post> posts = postRepository.getPostsByAuthUser(authUser, PageRequest.of(page-1, 20));
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : posts) {
            postResponseDtos.add(postMapper.toDto(post, reactionService.getTotalReactionsOfPost(post)));
        }

        return postResponseDtos;
    }

    public List<PostResponseDto> getPostsByUsername(String username, int page) {
        User authUser = userService.getUserOrThrows();
        boolean isFriend = invitationRepository.isFriend(authUser.getUsername(), username);
        PageRequest pageRequest = PageRequest.of(page-1, 20);
        List<Post> posts;
        if (isFriend) {
            posts = postRepository.getPostsIfIsFriend(username, pageRequest);

        } else {
            posts = postRepository.getPostsIfIsNotFriend(username, pageRequest);
        }

        return posts.stream().map(post -> postMapper.toDto(post, reactionService.getTotalReactionsOfPost(post))).toList();
    }

    public PostResponseDto createPost(PostRequestDto dto) {
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
        post = postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public void updatePost(Long postId, PostRequestDto dto) {
        Post post = getPost(postId);
        List<Attachment> attachments = post.getAttachments();

        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            List<UpdateFileDto> updateFileDtos = new ArrayList<>();
            for (int i=0; i<dto.getAttachments().size(); i++) {
                UploadFileDto uploadFileDto = dto.getAttachments().get(i);
                Attachment attachment = attachments.get(i);

                String source = attachment.getSource();
                MultipartFile file = uploadFileDto.getFile();
                updateFileDtos.add(new UpdateFileDto(source, file));

                attachment.setName(file.getOriginalFilename());
                attachment.setDescription(uploadFileDto.getDescription());
            }
            cloudinaryService.updateMultiFile(updateFileDtos);
            post.setAttachments(attachments);
        }
        post.setCaption(dto.getCaption());
        post.setVisibility(dto.getVisibility());
        post.setCaptionBackground(dto.getCaptionBackground());
        post.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));

        postRepository.save(post);
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
        postRepository.save(newPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getNewsFeed(int page) {
        List<Post> topPostReactions = postRepository.getPostsByTopReaction(PageRequest.of(page - 1, 5));
        List<UserWithAvatarDto> friends = userService.getListFriend();

        List<Post> latestFriendPosts = new ArrayList<>();
        friends.forEach(friend -> {
            if (friend != null) {
                List<Post> posts = postRepository.getNewPostByUserId(friend.getId(), PageRequest.of(page - 1, 1));
                if (posts != null && !posts.isEmpty() && posts.getFirst() != null) {
                    latestFriendPosts.add(posts.getFirst());
                }
            }
        });

        List<Post> mergedPosts = Stream.concat(
                topPostReactions.stream().filter(Objects::nonNull),
                latestFriendPosts.stream().filter(Objects::nonNull)
        ).toList();

        List<Post> shuffled = new ArrayList<>(mergedPosts);
        Collections.shuffle(shuffled);

        return shuffled.stream()
                .filter(Objects::nonNull)
                .map(post -> postMapper.toDto(post, reactionService.getTotalReactionsOfPost(post)))
                .toList();
    }
}
