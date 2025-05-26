package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Attachment;

public record AttachmentDto(
    Long id,
    String source,
    Attachment.FileType type
) {

    public AttachmentDto(Attachment attachment) {
        this(attachment.getId(), attachment.getSource(), attachment.getType());
    }
}
