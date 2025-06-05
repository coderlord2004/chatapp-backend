package com.group4.chatapp.dtos.invitation;

import com.group4.chatapp.dtos.ChatRoomDto;
import org.springframework.lang.Nullable;

public record ReplyResponse(@Nullable ChatRoomDto newChatRoom) {}
