package com.group4.chatapp.mappers;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserWithAvatarDto toDto(User user);

    User toUser(UserWithAvatarDto userWithAvatarDto);
}
