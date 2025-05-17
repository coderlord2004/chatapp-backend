package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.services.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/messages/")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{roomId}")
    public List<MessageReceiveDto> getMessages(@PathVariable long roomId, @RequestParam(name = "page", defaultValue = "1") int page) {
        return messageService.getMessages(roomId, page);
    }

    @PostMapping("/{roomId}")
    public void sendMessage(
        @PathVariable long roomId,
        @Valid @RequestBody MessageSendDto dto
    ) {
        messageService.sendMessage(roomId, dto);
    }
}
