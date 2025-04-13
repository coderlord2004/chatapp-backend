package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages/")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/{roomId}")
    public void sendMessage(
        @PathVariable long roomId,
        @Valid @RequestBody MessageSendDto dto
    ) {
        messageService.sendMessage(roomId, dto);
    }
}
