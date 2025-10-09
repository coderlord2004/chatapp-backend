package com.group4.chatapp.dtos.invitation;


import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.UserRelation;
import org.springframework.lang.Nullable;

public record UserRelationDto(

    long id,

    UserWithAvatarDto sender,
    UserWithAvatarDto receiver,

    @Nullable
    Long chatRoomId,

    UserRelation.Status status,
    Boolean isBlocking
) {

    public UserRelationDto(UserRelation userRelation) {

        this(
            userRelation.getId(),
            new UserWithAvatarDto(userRelation.getSender()),
            new UserWithAvatarDto(userRelation.getReceiver()),

            userRelation.getChatRoom() == null
                ? null
                : userRelation.getChatRoom().getId(),

            userRelation.getStatus(),
            userRelation.getIsBlocking()
        );
    }
}
