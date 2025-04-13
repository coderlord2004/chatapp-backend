package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.dtos.FileDto;
import com.group4.chatapp.models.ChatMessage;

import java.sql.Timestamp;
import java.util.List;

public record MessageReceiveDto(
    String sender,
    String message,
    Timestamp sentOn,
    List<FileDto> attachments
) {

    public MessageReceiveDto(ChatMessage message) {

        this(
            message.getSender().getUsername(),
            message.getMessage(),
            message.getSentOn(),
            message.getAttachments()
                .stream()
                .map(FileDto::new)
                .toList()
        );
    }
}
