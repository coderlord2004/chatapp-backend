package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.SignalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/signaling")
    public void handleSignaling(SignalMessage message, Principal principal) {
        String sender = principal.getName();
        String target = message.getTarget();

        log.info("WebRTC signal from {} to {} - type: {}", sender, target, message.getType());

        message.setCaller(sender);

        messagingTemplate.convertAndSendToUser(
                target,
                "/queue/signaling", // CHỈ CẦN phần đuôi (không thêm tên sender ở đây)
                message
        );
    }

}
