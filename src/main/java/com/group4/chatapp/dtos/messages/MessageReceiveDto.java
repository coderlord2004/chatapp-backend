package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.dtos.attachment.AttachmentDto;
import com.group4.chatapp.models.ChatMessage;

import java.sql.Timestamp;
import java.util.List;

public record MessageReceiveDto(
    long id,
    String sender,
    String message,
    Timestamp sentOn,
    List<AttachmentDto> attachments
) {

    public MessageReceiveDto(ChatMessage message) {

        this(
            message.getId(),
            message.getSender().getUsername(),
            message.getMessage(),
            message.getSentOn(),
            message.getAttachments()
                .stream()
                .map(AttachmentDto::new)
                .toList()
        );
    }
}
