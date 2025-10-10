package com.group4.chatapp.services;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.mappers.NotificationMapper;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class NotificationService {
    private NotificationRepository notificationRepository;
    private NotificationMapper notificationMapper;

    public List<NotificationResponseDto> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream().map(notificationMapper::toDto).toList();
    }
}
