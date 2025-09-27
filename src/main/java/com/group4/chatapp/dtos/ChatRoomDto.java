package com.group4.chatapp.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDto {

    private long id;

    @Nullable
    private String name;

    private AttachmentDto avatar;
    private List<UserWithAvatarDto> members;

    private ChatRoom.Type type;
    private Timestamp createdOn;

    private MessageReceiveDto latestMessage;
    private List<MessageReceiveDto> firstMessagePage;

    public ChatRoomDto(ChatRoom room,@Nullable ChatMessage latestMessage) {

        this(
            room.getId(),
            room.getName(),
            null,
            room.getMembers().stream().map(UserWithAvatarDto::new).toList(),
            room.getType(),
            room.getCreatedOn(),
            null,
            null
        );

        var avatar = room.getAvatar();
        if (avatar != null) {
            this.avatar = new AttachmentDto(avatar);
        }

        if (latestMessage != null) {
            this.latestMessage = new MessageReceiveDto(latestMessage);
        }
    }

    public ChatRoomDto(ChatRoom room, List<ChatMessage> firstMessagePage) {
        this(
                room.getId(),
                room.getName(),
                null,
                room.getMembers().stream().map(UserWithAvatarDto::new).toList(),
                room.getType(),
                room.getCreatedOn(),
                null,
                firstMessagePage.stream().map(MessageReceiveDto::new).toList()
        );

        var avatar = room.getAvatar();
        if (avatar != null) {
            this.avatar = new AttachmentDto(avatar);
        }
    }
}
