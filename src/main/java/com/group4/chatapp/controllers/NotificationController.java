package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/get-all/")
    public List<NotificationResponseDto> getNotifications() {
        return notificationService.getNotifications();
    }

    @DeleteMapping("/delete/")
    public ResponseEntity<String> deleteNotification(@RequestParam("id") Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Delete notification successfully!");
    }
}
