package com.group4.chatapp.dtos;

import com.group4.chatapp.dtos.user.UserWithRelationDto;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionResponseDto {
    private UserWithRelationDto relation;
    private ReactionType reacted;

    public ReactionResponseDto(User user, Long totalFollowers, Long totalFollowing, Long totalPosts, Long totalFriends, Invitation invitation, ReactionType reactionType) {
        this.relation = new UserWithRelationDto(user, totalFollowers, totalFollowing, totalPosts, totalFriends, invitation);
        this.reacted = reactionType;
    }
}
