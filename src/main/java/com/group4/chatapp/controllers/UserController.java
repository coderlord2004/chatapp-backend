package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.post.PostCreationRequestDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.token.TokenRefreshRequestDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserWithInvitationDto;
import com.group4.chatapp.models.User;
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
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtsService jwtsService;

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

    @GetMapping("/info/")
    public UserWithAvatarDto getUser() {
        User user = userService.getUserOrThrows();

        return new UserWithAvatarDto(user);
    }

    @GetMapping("/friends/")
    public List<UserWithAvatarDto> getListFriend() {
        return userService.getListFriend();
    }

    @GetMapping("/search/")
    public List<UserWithInvitationDto> searchUser(
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

    @GetMapping("/posts/get/")
    public List<PostResponseDto> getPosts(@RequestParam(value = "page", defaultValue = "1") int page){
        return userService.getPosts(page);
    }

    @PostMapping(value = "/posts/create/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponseDto createPost(@ModelAttribute PostCreationRequestDto dto) {
        return userService.createPost(dto);
    }
}
