package com.group4.chatapp.dtos.user;

import com.group4.chatapp.models.User;

public record UserSearchDto(

    long id,

    String username
) {

    public UserSearchDto(User user) {
        this(user.getId(), user.getUsername());
    }
}
