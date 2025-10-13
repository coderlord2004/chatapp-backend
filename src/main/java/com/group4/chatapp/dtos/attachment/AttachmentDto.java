package com.group4.chatapp.dtos.attachment;

import com.group4.chatapp.models.Attachment;

public record AttachmentDto(
        Long id,
        String name,
        String source,
        Attachment.FileType type,
        String format,
        String description
) {

    public AttachmentDto(Attachment attachment) {
        this(
                attachment.getId(),
                attachment.getName(),
                attachment.getSource(),
                attachment.getType(),
                attachment.getFormat(),
                attachment.getDescription()
        );
    }
}
