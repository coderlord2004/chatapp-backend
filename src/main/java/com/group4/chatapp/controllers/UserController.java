package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.attachment.AttachmentDto;
import com.group4.chatapp.dtos.comment.UserCommentDto;
import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.token.TokenRefreshRequestDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserInformationDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserWithRelationDto;
import com.group4.chatapp.services.CommentService;
import com.group4.chatapp.services.JwtsService;
import com.group4.chatapp.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtsService jwtsService;
    private final CommentService commentService;

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto dto) {
        userService.createUser(dto);
    }

    @PostMapping("/token/")
    public TokenObtainPairDto obtainToken(@Valid @RequestBody UserDto dto) {
        return jwtsService.tokenObtainPair(dto);
    }

    @PostMapping("/token/refresh/")
    public TokenRefreshDto refreshToken(
        @Valid @RequestBody TokenRefreshRequestDto dto
    ) {
        return jwtsService.refreshToken(dto.refresh());
    }

    @GetMapping("/my-info/")
    public UserInformationDto getAuthUser() {
        return userService.getAuthUser();
    }
    
    @GetMapping("/info/")
    public UserWithRelationDto getUser(@RequestParam("username") String username) {
        return userService.getUser(username);
    }

    @GetMapping("/friends/")
    public List<UserWithAvatarDto> getListFriend() {
        return userService.getOnlineFriends();
    }

    @GetMapping("/search/")
    public List<UserWithRelationDto> searchUser(
        @RequestParam(name = "q") String keyword,
        @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        return userService.searchUser(keyword, page);
    }

    @PostMapping(value = "/avatar/update/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> updateAvatar(@NotNull @RequestPart("avatar") MultipartFile avatar) {
        return Map.of("avatar_url", userService.updateAvatar(avatar));
    }

    @PostMapping("/cover-picture/update/")
    public Map<String, String> updateCoverPicture(@NotNull @RequestPart("coverPicture") MultipartFile coverPicture) {
        return Map.of("cover_picture_url", userService.updateCoverPicture(coverPicture));
    }

    @GetMapping("/friend-suggestions/")
    public List<UserWithAvatarDto> getFriendSuggestions(@RequestParam(value = "page", defaultValue = "1") int page) {
        return userService.suggestFriend(page);
    }

    @GetMapping("/medias/")
    public List<AttachmentDto> getUserMedias(
            @NotNull @RequestParam("userId") Long userId,
            @NotNull @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return userService.getUserMedias(userId, page - 1);
    }

    @GetMapping("/comments/")
    public List<UserCommentDto> getUserComments(@RequestParam("page") int page) {
        return commentService.getUserComments(page - 1);
    }

    @PostMapping("/block/")
    public void blockUser(@RequestParam("userId") Long userId) {
        userService.blockUser(userId);
    }

    @PostMapping("/un-block/")
    public void ubBlockUser(@RequestParam("userId") Long userId) {
        userService.unBlockUser(userId);
    }
}
