package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invitations/")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public List<InvitationDto> listInvitations() {
        return invitationService.getInvitations();
    }
}
