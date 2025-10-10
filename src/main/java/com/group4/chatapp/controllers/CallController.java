package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.callInvitation.CallInvitationSendDto;
import com.group4.chatapp.services.CallService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirements({
        @SecurityRequirement(name = "basicAuth"),
        @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/call")
@RequiredArgsConstructor
public class CallController {
    private final CallService callService;

    @PostMapping("/invitation/send/")
    public void sendInvitationInChannel(@RequestBody CallInvitationSendDto dto) {
        callService.sendInvitationToChannel(dto);
    }

    @PostMapping("/invitation/cancel/")
    public void cancelCallInvitation(@RequestBody CallInvitationSendDto dto) {
        callService.cancelCallInvitation();
    }

    @PostMapping("/invitation/refuse/")
    public void refuseCallInvitation(@RequestParam("caller") String caller) {
        callService.refuseCallInvitation(caller);
    }
}
