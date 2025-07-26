package com.group4.chatapp.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirements({
        @SecurityRequirement(name = "basicAuth"),
        @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/video-call/")
@RequiredArgsConstructor
public class VideoCallController {

    @PostMapping
    public void sendInvitationInChannel() {

    }
}
