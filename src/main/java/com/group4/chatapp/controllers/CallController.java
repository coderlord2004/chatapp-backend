package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.callInvitation.CallInvitationSendDto;
import com.group4.chatapp.services.CallService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirements({
        @SecurityRequirement(name = "basicAuth"),
        @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/call/")
@RequiredArgsConstructor
public class CallController {
    private final CallService callService;

    @PostMapping("/invitation/")
    public void sendInvitationInChannel(@RequestBody CallInvitationSendDto dto) {
        callService.sendInvitationToChannel(dto);
    }
}
