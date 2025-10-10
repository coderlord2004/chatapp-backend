package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/get-all/")
    public List<NotificationResponseDto> getNotifications(@RequestParam("userId") Long userId) {
        return notificationService.getNotificationsByUserId(userId);
    }
}
