package com.group4.chatapp.dtos.user;

import jakarta.validation.constraints.NotEmpty;

public record UserDto(

    @NotEmpty
    String username,

    @NotEmpty
    String password

) {}
