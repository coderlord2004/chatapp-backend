package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Attachment;

public record FileDto(
    String source,
    Attachment.FileType type
) {

    public FileDto(Attachment attachment) {
        this(attachment.getSource(), attachment.getType());
    }
}
