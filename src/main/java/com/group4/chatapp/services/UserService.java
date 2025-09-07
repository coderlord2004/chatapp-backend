package com.group4.chatapp.services;

import com.group4.chatapp.dtos.PostAttachmentResponseDto;
import com.group4.chatapp.dtos.post.PostCreationRequestDto;
import com.group4.chatapp.dtos.PostAttachmentDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserWithInvitationDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.PostAttachment.PostAttachment;
import com.group4.chatapp.models.PostAttachment.PostAttachmentID;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class UserService {
    private CloudinaryService cloudinaryService;
    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private FileTypeService fileTypeService;
    private AttachmentService attachmentService;
    private PostRepository postRepository;
    private PostAttachmentRepository postAttachmentRepository;
    private ReactionRepository reactionRepository;
    private CommentRepository commentRepository;
    private ShareRepository shareRepository;

    public void createUser(UserDto dto) {

        if (repository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists. Please try a different one."
            );
        }

        var hashedPassword = passwordEncoder.encode(dto.password());

        var user = User.builder()
            .username(dto.username())
            .password(hashedPassword)
            .build();

        repository.save(user);
    }

    public List<UserWithAvatarDto> getListFriend () {
        User authUser = getUserOrThrows();
        List<Object[]> friends = repository.getListFriend(authUser.getId());

        return friends.stream().map(pair -> {
            User sender = (User) pair[0];
            User receiver = (User) pair[1];
            User friend = Objects.equals(authUser.getId(), sender.getId()) ? receiver : sender;

            return new UserWithAvatarDto(friend);
        }).toList();
    }

    public Optional<User> getUserByAuthentication(@Nullable Authentication authentication) {

        if (authentication == null) {
            return Optional.empty();
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String username = jwt.getSubject();
            return repository.findByUsername(username);
        } else if (principal instanceof User user) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByAuthentication(authentication);
    }

    public User getUserOrThrows() {
        return getUserByContext()
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
    }

    @Transactional(readOnly = true)
    public List<UserWithInvitationDto> searchUser(String keyword, int page) {

        var MAX_LIMIT = 20;

        if (page <= 0) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                String.format("Limit must be between 1 and %d", MAX_LIMIT)
            );
        }

        var pageable = PageRequest.of(page - 1, 20);
        User authUser = getUserOrThrows();

        Page<Object[]> results = repository.searchUsersWithInvitations(authUser, keyword, pageable);
        List<UserWithInvitationDto> responses = new ArrayList<>();
        for (Object[] result : results) {
            User user = (User) result[0];
            Invitation invitation = (Invitation) result[1];
            UserWithInvitationDto userWithInvitationDto = new UserWithInvitationDto(user, invitation);
            responses.add(userWithInvitationDto);
        }
        return responses;
    }

    public String updateAvatar(MultipartFile avatar) {
        if(!fileTypeService.isImage(avatar) || avatar.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "File is invalid!"
            );
        }
        User user = getUserOrThrows();
        String avatarUrl = cloudinaryService.uploadFile(avatar);
        user.setAvatar(avatarUrl);
        repository.save(user);

        return avatarUrl;
    }

    public String updateCoverPicture(MultipartFile coverPicture) {
        if(!fileTypeService.isImage(coverPicture) && coverPicture.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "File is invalid!"
            );
        }
        User user = getUserOrThrows();
        String coverPictureUrl = cloudinaryService.uploadFile(coverPicture);
        user.setCoverPicture(coverPictureUrl);
        repository.save(user);

        return coverPictureUrl;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(int page) {
        User authUser = getUserOrThrows();
        List<Post> posts = postRepository.findPostsByUser(authUser, PageRequest.of(page-1, 20));
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for (Post post : posts) {
            Long totalReactions = reactionRepository.countTotalReactions(post.getId(), TargetType.POST);
            Long totalComments = commentRepository.getTotalComments(post.getId(), TargetType.POST);
            Long totalShares = shareRepository.getTotalShares(post.getId(), TargetType.POST);
            List<ReactionType> reactionTypes = reactionRepository.getTopReactionType(post.getId(), TargetType.POST, PageRequest.of(0, 3));
            postResponseDtos.add(new PostResponseDto(post, totalReactions, reactionTypes, totalComments, totalShares));
        }

        return postResponseDtos;
    }

    public PostResponseDto createPost(PostCreationRequestDto dto) {
        if (dto.getCaption() == null && dto.getAttachments() == null) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Caption and Attachments are not null!"
            );
        }

        User authUser = getUserOrThrows();
        assert dto.getAttachments() != null;

        List<MultipartFile> files = new ArrayList<>();
        dto.getAttachments().forEach(postAttachmentDto -> {
            files.add(postAttachmentDto.getAttachment());
        });
        List<Attachment> attachments = attachmentService.getAttachments(files);
        Post post = Post.builder()
                .caption(dto.getCaption())
                .captionBackground(dto.getCaptionBackground())
                .visibility(PostVisibilityType.valueOf(dto.getVisibility()))
                .user(authUser)
                .build();
        post = postRepository.save(post);
        List<PostAttachmentResponseDto> postAttachmentResponseDtos = new ArrayList<>();
        for (int i=0; i<dto.getAttachments().size(); i++) {
            PostAttachmentDto postAttachmentDto = dto.getAttachments().get(i);
            Attachment attachment = attachments.get(i);
            if (attachment != null) {
                PostAttachmentID id = new PostAttachmentID(post.getId(), attachment.getId());
                PostAttachment postAttachment = PostAttachment.builder()
                        .id(id)
                        .post(post)
                        .attachment(attachment)
                        .description(postAttachmentDto.getDescription())
                        .build();
                postAttachmentRepository.save(postAttachment);

                PostAttachmentResponseDto postAttachmentResponseDto = PostAttachmentResponseDto.builder()
                        .description(postAttachmentDto.getDescription())
                        .attachmentUrl(attachment.getSource())
                        .attachmentType(String.valueOf(attachment.getType()))
                        .build();
                postAttachmentResponseDtos.add(postAttachmentResponseDto);
            }
        }

        return PostResponseDto.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .createdOn(post.getCreatedOn())
                .captionBackground(post.getCaptionBackground())
                .attachments(postAttachmentResponseDtos)
                .build();
    }
}
