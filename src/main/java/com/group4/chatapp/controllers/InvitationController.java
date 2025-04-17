package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.dtos.invitation.InvitationReplyDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.services.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/invitations/")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public List<InvitationDto> listInvitations() {
        return invitationService.getInvitations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendInvitation(@Valid @RequestBody InvitationSendDto dto) {
        invitationService.sendInvitation(dto.receiverUserName());
    }

    @PatchMapping("/{invitationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replyInvitation(
        @PathVariable long invitationId,
        @Valid @RequestBody InvitationReplyDto dto
    ) {
        invitationService.replyInvitation(invitationId, dto.accept());
    }
}
