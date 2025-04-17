package com.group4.chatapp.dtos.invitation;

import jakarta.validation.constraints.NotNull;

public record InvitationReplyDto(@NotNull Boolean accept) {}
