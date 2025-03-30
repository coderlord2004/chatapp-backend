package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/api/messages/")
    public void sendMessage(@Valid @RequestBody MessageSendDto dto) {

        var sender = userService.getUserByContext().orElseThrow();

        simpMessagingTemplate.convertAndSendToUser(
            dto.receiver(), "/queue/chat",
            new MessageReceiveDto(sender.getUsername(), dto.message())
        );
    }
}
