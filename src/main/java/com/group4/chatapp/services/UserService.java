package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserInformationDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserWithRelationDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.mappers.UserMapper;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class UserService {
    private CloudinaryService cloudinaryService;
    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private FileTypeService fileTypeService;
    private InvitationRepository invitationRepository;
    private PostRepository postRepository;
    private UserMapper userMapper;

    private SimpMessagingTemplate messagingTemplate;

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

    public UserInformationDto getAuthUser() {
        User user = getUserOrThrows();
        Long totalFollowers = invitationRepository.countFollowersByUserId(user.getId());
        Long totalFollowing = invitationRepository.countFollowingByUserId(user.getId());
        Long totalPosts = postRepository.countPostByUserId(user.getId());
        return new UserInformationDto(user, totalFollowers, totalFollowing, totalPosts);
    }

    public Long[] getUserStatistics(Long userId) {
        Long totalFollowers = invitationRepository.countFollowersByUserId(userId);
        Long totalFollowing = invitationRepository.countFollowingByUserId(userId);
        Long totalPosts = postRepository.countPostByUserId(userId);
        return new Long[]{totalFollowers, totalFollowing, totalPosts};
    }

    public UserWithRelationDto getUser(String username) {
        User authUser = getUserOrThrows();

        if (authUser.getUsername().equals(username)) return null;

        User user = getUserByUsername(username);
        Invitation invitation = invitationRepository.findBySenderIdAndReceiverId(authUser.getId(), user.getId());

        Long[] userStatistics = getUserStatistics(user.getId());
        return new UserWithRelationDto(user, userStatistics[0], userStatistics[1], userStatistics[2], invitation);
    }

    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "User is not found!"
        ));
    }

    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "User is not found!"
        ));
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

    public List<UserWithAvatarDto> getOnlineFriends () {
        User authUser = getUserOrThrows();
        List<Object[]> friends = repository.getOnlineFriends(authUser.getId());

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
    public List<UserWithRelationDto> searchUser(String keyword, int page) {

        var MAX_LIMIT = 20;

        if (page <= 0) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                String.format("Limit must be between 1 and %d", MAX_LIMIT)
            );
        }

        var pageable = PageRequest.of(page - 1, 20);
        User authUser = getUserOrThrows();

        Page<Object[]> results = repository.searchUsersWithInvitation(authUser, keyword, pageable);
        List<UserWithRelationDto> responses = new ArrayList<>();
        for (Object[] result : results) {
            User user = (User) result[0];
            Long[] userStatistics = getUserStatistics(user.getId());
            Invitation invitation = (Invitation) result[1];
            UserWithRelationDto userWithRelationDto = new UserWithRelationDto(user, userStatistics[0], userStatistics[1], userStatistics[2], invitation);
            responses.add(userWithRelationDto);
        }
        return responses;
    }

    public String updateAvatar(MultipartFile avatar) {
        if(fileTypeService.isImage(avatar) || avatar.isEmpty()) {
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
        if(fileTypeService.isImage(coverPicture) && coverPicture.isEmpty()) {
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

    public List<UserWithAvatarDto> suggestFriend(int page) {
        User authUser = getUserOrThrows();
        List<User> suggestedFriends = repository.getUserIsNotFriend(authUser.getId(), PageRequest.of(page-1, 20));
        return suggestedFriends.stream().map(userMapper::toDto).toList();
    }

    public void blockUser(Long userId) {
        User authUser = getUserOrThrows();
        User otherUser = getUserById(userId);
        Invitation invitation = invitationRepository.findBySenderIdAndReceiverId(authUser.getId(), otherUser.getId());
        if (invitation == null) {
            Invitation newInvitation = Invitation.builder()
                    .sender(authUser)
                    .receiver(otherUser)
                    .restriction(Invitation.RestrictionType.BLOCKED)
                    .build();
            invitationRepository.save(newInvitation);
        } else {
            invitation.setRestriction(Invitation.RestrictionType.BLOCKED);
            invitationRepository.save(invitation);
        }

        messagingTemplate.convertAndSendToUser(
                otherUser.getUsername(),
                "/queue/blocking/",
                Map.of(
                        "is_blocked", true
                )
        );
    }

    public void unBlockUser(Long userId) {
        User authUser = getUserOrThrows();
        User otherUser = getUserById(userId);
        Invitation invitation = invitationRepository.findBySenderIdAndReceiverId(authUser.getId(), otherUser.getId());

        if (invitation == null) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "You haven't blocked this user before!"
            );
        } else {
            invitation.setRestriction(Invitation.RestrictionType.NONE);
            invitationRepository.save(invitation);

            messagingTemplate.convertAndSendToUser(
                    otherUser.getUsername(),
                    "/queue/blocking/",
                    Map.of(
                            "is_blocked", false
                    )
            );
        }
    }
}
