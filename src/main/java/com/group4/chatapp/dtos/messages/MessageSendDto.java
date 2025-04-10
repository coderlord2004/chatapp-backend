package com.group4.chatapp.dtos.messages;

import jakarta.validation.constraints.NotEmpty;

public record MessageSendDto(

    @NotEmpty
    String message

) {}
