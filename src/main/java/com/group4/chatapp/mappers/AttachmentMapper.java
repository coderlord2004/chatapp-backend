package com.group4.chatapp.mappers;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.models.Attachment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentDto toDto(Attachment attachment);

    List<AttachmentDto> toDtoList(List<Attachment> attachments);
}
