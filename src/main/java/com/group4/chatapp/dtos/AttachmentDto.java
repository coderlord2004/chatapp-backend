package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Attachment;

public record AttachmentDto(
    String source,
    Attachment.FileType type
) {

    public AttachmentDto(Attachment attachment) {
        this(attachment.getSource(), attachment.getType());
    }
}
