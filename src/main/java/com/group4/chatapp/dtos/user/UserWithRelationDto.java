package com.group4.chatapp.dtos.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.invitation.UserRelationDto;
import com.group4.chatapp.models.UserRelation;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWithRelationDto {
    private UserRelationDto relation;
    private UserWithAvatarDto userWithAvatar;
    private UserInformationDto userWithInformation;

    public UserWithRelationDto(User user, Long totalFollowers, Long totalFollowing, Long totalPosts, UserRelation userRelation) {
        if (userRelation == null) {
            this.userWithAvatar = new UserWithAvatarDto(user);
        } else {
            this.relation = new UserRelationDto(userRelation);

            if (userRelation.getIsBlocking()) {
                this.userWithAvatar = new UserWithAvatarDto(user);
            } else {
                this.userWithInformation = new UserInformationDto(user, totalFollowers, totalFollowing, totalPosts);
            }
        }
    }
}
