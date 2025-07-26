package com.group4.chatapp.controllers;

import com.group4.chatapp.services.AgoraTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agora")
@RequiredArgsConstructor
public class AgoraTokenController {
    private final AgoraTokenService agoraTokenService;

    @GetMapping("/token")
    public ResponseEntity<?> getAgoraToken(
            @RequestParam String channelName,
            @RequestParam String uid
    ) {
        return ResponseEntity.ok(Map.of("token", agoraTokenService.generateAgoraToken(channelName, uid)));
    }
}
