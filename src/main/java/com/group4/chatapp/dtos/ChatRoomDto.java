package com.group4.chatapp.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.attachment.AttachmentDto;
import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
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
    private UserWithAvatarDto leader;
    private Boolean leaderOnlySend;
    private List<UserWithAvatarDto> members;

    private ChatRoom.Type type;
    private Timestamp createdOn;
    private Boolean isWaitingRoom;

    private MessageReceiveDto latestMessage;
    private List<MessageReceiveDto> firstMessagePage;

    public ChatRoomDto(ChatRoom room) {
        this.id = room.getId();
        this.name = room.getName();
        this.leaderOnlySend = room.getLeaderOnlySend();
        this.members = room.getMembers().stream().map(UserWithAvatarDto::new).toList();
        this.type = room.getType();
        this.createdOn = room.getCreatedOn();
        this.isWaitingRoom = room.getIsWaitingRoom();

        if (room.getAvatar() != null) {
            this.avatar = new AttachmentDto(room.getAvatar());
        }
        if (room.getLeader() != null) {
            this.leader = new UserWithAvatarDto(room.getLeader());
        }
    }

    public ChatRoomDto(ChatRoom room, @Nullable ChatMessage latestMessage) {
        this(room);
        if (latestMessage != null) {
            this.latestMessage = new MessageReceiveDto(latestMessage);
        }
    }

    public ChatRoomDto(ChatRoom room, List<ChatMessage> firstMessagePage) {
        this(room);
        if (firstMessagePage != null && !firstMessagePage.isEmpty()) {
            this.firstMessagePage = firstMessagePage.stream()
                    .map(MessageReceiveDto::new)
                    .toList();
        }
    }
}
