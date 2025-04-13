package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MessageSendDto(

    @NotEmpty
    String message

) {

    public ChatMessage toMessage(ChatRoom room, User sender) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .message(this.message)
                .attachments(List.of()) // TODO: handle message with attachments
                .build();
    }
}
