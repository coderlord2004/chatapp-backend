package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.invitation.UserRelationDto;
import com.group4.chatapp.dtos.invitation.InvitationReplyDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.dtos.invitation.ReplyResponse;
import com.group4.chatapp.services.invitations.UserRelationService;
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
public class UserRelationController {

    private final UserRelationService userRelationService;

    @GetMapping
    public List<UserRelationDto> listInvitations() {
        return userRelationService.getInvitations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void sendInvitation(@Valid @RequestBody InvitationSendDto dto) {
        userRelationService.sendInvitation(dto);
    }

    @PatchMapping("/{invitationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ReplyResponse replyInvitation(
        @PathVariable long invitationId,
        @Valid @RequestBody InvitationReplyDto dto
    ) {
        return userRelationService.replyInvitation(invitationId, dto.accept());
    }
}
