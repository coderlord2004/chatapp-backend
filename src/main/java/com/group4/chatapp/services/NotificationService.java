package com.group4.chatapp.services;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.mappers.NotificationMapper;
import com.group4.chatapp.models.Enum.NotificationType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class NotificationService {
    private NotificationRepository notificationRepository;
    private NotificationMapper notificationMapper;
    private UserService userService;
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<NotificationResponseDto> getNotifications() {
        User authUser = userService.getUserOrThrows();
        List<Notification> notifications = notificationRepository.findByReceiverId(authUser.getId());
        return notifications.stream().map(notificationMapper::toDto).toList();
    }

    public Notification create(Notification notification) {
        Long senderId = notification.getSender().getId();
        int totalNotification = notificationRepository.countByUserId(senderId);
        if (totalNotification >= 20) {
            Notification oldestNotification = notificationRepository.findOldestBySenderId(senderId, PageRequest.of(0, 1));
            notificationRepository.delete(oldestNotification);
        }
        return notificationRepository.save(notification);
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    public void notifyToUsers(User sourceUser, List<User> targetUsers, Notification notification) {
        for (User user : targetUsers) {
            notification.setSender(sourceUser);
            notification.setReceiver(user);

            Notification newNotification = create(notification);
            simpMessagingTemplate.convertAndSendToUser(
                    sourceUser.getUsername(),
                    "/queue/notification/",
                    newNotification
            );
        }
    }
}
