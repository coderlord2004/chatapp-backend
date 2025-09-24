package com.group4.chatapp.mappers;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.models.Attachment;

import java.util.List;

public interface AttachmentMapper {
    AttachmentDto toDto(Attachment attachment);

    List<AttachmentDto> toDtoList(List<Attachment> attachments);
}
