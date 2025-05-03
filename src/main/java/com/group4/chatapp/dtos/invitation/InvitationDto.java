package com.group4.chatapp.dtos.invitation;


import com.group4.chatapp.models.Invitation;
import org.springframework.lang.Nullable;

public record InvitationDto(

    long id,

    String sender,
    String receiver,

    @Nullable
    Long chatRoomId,

    Invitation.Status status
) {

    public InvitationDto(Invitation invitation) {
        this(
            invitation.getId(),
            invitation.getSender().getUsername(),
            invitation.getReceiver().getUsername(),

            invitation.getChatRoom() == null
                ? null
                : invitation.getChatRoom().getId(),

            invitation.getStatus()
        );
    }
}
