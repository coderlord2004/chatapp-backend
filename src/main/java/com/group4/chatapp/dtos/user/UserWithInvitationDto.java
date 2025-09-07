package com.group4.chatapp.dtos.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWithInvitationDto {
    private UserWithAvatarDto userDto;
    private InvitationDto invitationDto;

    public UserWithInvitationDto(User user, Invitation invitation) {
        this.userDto = new UserWithAvatarDto(user);
        this.invitationDto = invitation == null ? null : new InvitationDto(invitation);
    }
}
