package com.group4.chatapp.dtos;

import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomDto {

    private long id;

    @Nullable
    private String name;

    private FileDto avatar;
    private List<String> membersUsername;

    private ChatRoom.Type type;
    private Timestamp createdOn;

    public ChatRoomDto(ChatRoom room) {

        this(
            room.getId(),
            room.getName(),
            null,
            room.getMembers().stream().map(User::getUsername).toList(),
            room.getType(),
            room.getCreatedOn()
        );

        var avatar = room.getAvatar();
        if (avatar != null) {
            this.avatar = new FileDto(avatar);
        }
    }
}
