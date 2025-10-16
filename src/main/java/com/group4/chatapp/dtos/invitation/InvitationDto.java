package com.group4.chatapp.dtos.invitation;


import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.Invitation;
import org.springframework.lang.Nullable;

public record InvitationDto(

    long id,

    UserWithAvatarDto sender,
    UserWithAvatarDto receiver,

    @Nullable
    Long chatRoomId,

    Invitation.Status status,
    Invitation.RestrictionType restriction
) {

    public InvitationDto(Invitation invitation) {

        this(
            invitation.getId(),
            new UserWithAvatarDto(invitation.getSender()),
            new UserWithAvatarDto(invitation.getReceiver()),

            invitation.getChatRoom() == null
                ? null
                : invitation.getChatRoom().getId(),

            invitation.getStatus(),
            invitation.getRestriction()
        );
    }
}
