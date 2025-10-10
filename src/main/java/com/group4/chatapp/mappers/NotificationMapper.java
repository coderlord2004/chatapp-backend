package com.group4.chatapp.mappers;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.models.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface NotificationMapper {
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "receiver", source = "receiver")
    NotificationResponseDto toDto(Notification notification);
}
