package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Attachment;

public record AttachmentDto(
    String name,
    String source,
    Attachment.FileType type,
    String format,
    String description
) {

    public AttachmentDto(Attachment attachment) {
        this(
                attachment.getSource(),
                attachment.getSource(),
                attachment.getType(),
                attachment.getFormat(),
                attachment.getDescription()
        );
    }
}
