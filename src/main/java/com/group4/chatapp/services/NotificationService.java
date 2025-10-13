package com.group4.chatapp.services;

import com.group4.chatapp.dtos.notification.NotificationResponseDto;
import com.group4.chatapp.mappers.NotificationMapper;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        Long receiverId = notification.getReceiver().getId();
        int totalNotification = notificationRepository.countByUserId(receiverId);
        if (totalNotification >= 20) {
            Notification oldestNotification = notificationRepository.findOldestByReceiverId(receiverId, PageRequest.of(0, 1)).getFirst();
            notificationRepository.delete(oldestNotification);
        }
        return notificationRepository.save(notification);
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    public void notifyAndCreateToUsers(User sourceUser, List<User> targetUsers, Notification notification) {
        for (User user : targetUsers) {
            if (Objects.equals(sourceUser.getId(), user.getId())) continue;

            notification.setSender(sourceUser);
            notification.setReceiver(user);

            Notification newNotification = create(notification);
            simpMessagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/notification/",
                    notificationMapper.toDto(newNotification)
            );
        }
    }

    public void notifyAndCreateToUser(User sender, User receiver, Notification notification) {
        if (Objects.equals(sender.getId(), receiver.getId())) return;

        notification.setSender(sender);
        notification.setReceiver(receiver);

        Notification newNotification = create(notification);
        simpMessagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/notification/",
                notificationMapper.toDto(newNotification)
        );
    }
}
