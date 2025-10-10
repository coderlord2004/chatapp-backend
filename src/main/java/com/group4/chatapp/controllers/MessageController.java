package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.services.messages.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public List<MessageReceiveDto> getMessages(
        @RequestParam(name = "room") long roomId,
        @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        return messageService.getMessages(roomId, page);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSendResponseDto sendMessage(
        @RequestParam(name = "room") long roomId,
        @Valid @ModelAttribute MessageSendDto dto
    ) {
        dto.validate();
        return messageService.sendMessage(roomId, dto);
    }

    @PutMapping(value = "/{messageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeMessage(
        @PathVariable long messageId,
        @Valid @ModelAttribute MessageSendDto dto
    ) {
        dto.validate();
        messageService.changeMessage(messageId, dto);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recallMessage(@PathVariable long messageId) {
        messageService.deleteMessage(messageId);
    }
}
