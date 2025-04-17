package com.group4.chatapp.dtos.invitation;

import jakarta.validation.constraints.NotEmpty;

public record InvitationSendDto(@NotEmpty String receiverUserName) {}
