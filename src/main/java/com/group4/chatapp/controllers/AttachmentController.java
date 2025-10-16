package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.attachment.PostAttachmentResponseDto;
import com.group4.chatapp.services.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attachment")
@RequiredArgsConstructor
public class AttachmentController {
    private AttachmentService attachmentService;

    @GetMapping("/get/")
    public PostAttachmentResponseDto getAttachment(@RequestParam("id") Long id) {
        return new PostAttachmentResponseDto();
    }
}
