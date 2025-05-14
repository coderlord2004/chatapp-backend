package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.token.TokenRefreshRequestDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserSearchDto;
import com.group4.chatapp.services.JwtsService;
import com.group4.chatapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/search/")
    public List<UserSearchDto> searchUser(
        @RequestParam(name = "q") String keyword,
        @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return userService.searchUser(keyword, limit);
    }
}
