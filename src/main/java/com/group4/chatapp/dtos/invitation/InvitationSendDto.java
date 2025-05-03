package com.group4.chatapp.dtos.invitation;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.Nullable;

public record InvitationSendDto(

    @NotEmpty
    String receiverUserName,

    @Nullable
    Long chatGroupId

) {}
