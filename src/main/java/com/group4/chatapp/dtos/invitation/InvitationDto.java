package com.group4.chatapp.dtos.invitation;


import com.group4.chatapp.models.Invitation;

public record InvitationDto(

    long id,

    String sender,
    String receiver,

    Invitation.Status status
) {

    public InvitationDto(Invitation invitation) {
        this(
            invitation.getId(),
            invitation.getSender().getUsername(),
            invitation.getReceiver().getUsername(),
            invitation.getStatus()
        );
    }
}
