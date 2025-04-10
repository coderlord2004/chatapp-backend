package com.group4.chatapp.dtos.messages;

public record MessageReceiveDto(
    long roomId,
    String sender,
    String message
) {}
