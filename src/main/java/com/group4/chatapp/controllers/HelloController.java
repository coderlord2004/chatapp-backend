package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.GreetingDto;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final UserService userService;

    @GetMapping("/api/v1/hello/")
    public GreetingDto greeting() {

        var user = userService.getUserByContext();

        String message = "Hello, world!";
        if (user.isPresent()) {
            String username = user.get().getUsername();
            message = String.format("Hello, your username is %s.", username);
        }

        return new GreetingDto(message);
    }
}
