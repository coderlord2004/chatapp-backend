package com.group4.chatapp.dtos;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
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
public class ChatRoomDto {

    private long id;

    @Nullable
    private String name;

    private AttachmentDto avatar;
    private List<String> membersUsername;

    private ChatRoom.Type type;
    private Timestamp createdOn;

    private MessageReceiveDto latestMessage;

    public ChatRoomDto(ChatRoom room,@Nullable ChatMessage latestMessage) {

        this(
            room.getId(),
            room.getName(),
            null,
            room.getMembers().stream().map(User::getUsername).toList(),
            room.getType(),
            room.getCreatedOn(),
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
}
