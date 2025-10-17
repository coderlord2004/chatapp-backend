package com.group4.chatapp.dtos.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWithRelationDto {
    private InvitationDto invitation;
    private UserWithAvatarDto userWithAvatar;
    private UserInformationDto userWithInformation;

    public UserWithRelationDto(User user, Long totalFollowers, Long totalFollowing, Long totalPosts, Long totalFriends, Invitation invitation) {
        if (invitation == null) {
            this.userWithInformation = new UserInformationDto(user, totalFollowers, totalFollowing, totalPosts, totalFriends);
        } else {
            this.invitation = new InvitationDto(invitation);
            if (invitation.isBlock()) {
                this.userWithAvatar = new UserWithAvatarDto(user);
            } else {
                this.userWithInformation = new UserInformationDto(user, totalFollowers, totalFollowing, totalPosts, totalFriends);
            }
        }
    }
}
