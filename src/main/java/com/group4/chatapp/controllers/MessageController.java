package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/api/messages/{roomId}")
    public void sendMessage(
        @PathVariable long roomId,
        @Valid @RequestBody MessageSendDto dto
    ) {
        messageService.sendMessage(roomId, dto);
    }
}
