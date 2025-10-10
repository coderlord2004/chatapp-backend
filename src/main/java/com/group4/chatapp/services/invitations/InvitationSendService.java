package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Enum.NotificationType;
import com.group4.chatapp.models.Notification;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.NotificationRepository;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
class InvitationSendService {
    private UserService userService;

    private InvitationRepository repository;
    private ChatRoomRepository chatRoomRepository;
    private InvitationCheckService invitationCheckService;
    private NotificationRepository notificationRepository;

    private SimpMessagingTemplate messagingTemplate;

    private void notifyInvitation(Invitation invitation) {
        messagingTemplate.convertAndSendToUser(
            invitation.getReceiver().getUsername(),
            "/queue/invitations/",
            new InvitationDto(invitation)
        );
    }

    private User getReceiverAndValidate(User sender, String receiverUsername) {

        var isSelfInvitation = sender.getUsername().equals(receiverUsername);

        if (isSelfInvitation) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You cannot invite yourself!"
            );
        }
        User receiver = userService.getUserByUsername(receiverUsername);
        invitationCheckService.checkSenderPermission(sender, receiver);

        return receiver;
    }

    private void validateFriendRequest(User sender, User receiver) {

        var areFriends = chatRoomRepository.usersShareRoomOfType(
            sender.getId(),
            receiver.getId(),
            ChatRoom.Type.DUO
        );

        if (areFriends) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You and the receiver are already friends."
            );
        }

        var isReceiverSent = repository.existsFriendRequestWith(
            receiver.getId(),
            sender.getId(),
            Invitation.Status.PENDING
        );

        if (isReceiverSent) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Please accept your receiver's invitation instead."
            );
        }
    }

    private void validateGroupRequest(User sender, User receiver, ChatRoom chatRoom) {

        var senderIsInTheRoom = chatRoomRepository.userIsMemberInChatRoom(
            sender.getId(), chatRoom.getId()
        );

        if (!senderIsInTheRoom) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this room."
            );
        }

        var receiverInChatRoom = chatRoomRepository.userIsMemberInChatRoom(
            receiver.getId(), chatRoom.getId()
        );

        if (receiverInChatRoom) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "The receiver is already in this room."
            );
        }
    }

    private boolean hasTheSamePendingInvitationBefore(
        User sender, User receiver, @Nullable ChatRoom chatRoom
    ) {
        if (chatRoom == null) {
            return repository.existsFriendRequestWith(
                sender.getId(),
                receiver.getId(),
                Invitation.Status.PENDING
            );
        } else {
            return repository.existGroupInvitationWith(
                sender.getId(),
                receiver.getId(),
                chatRoom.getId(),
                Invitation.Status.PENDING
            );
        }
    }

    private void validateChatRoomAndUsers(
        User sender, User receiver, @Nullable ChatRoom chatRoom
    ) {

        if (hasTheSamePendingInvitationBefore(sender, receiver, chatRoom)) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You sent the same invitation before."
            );
        }

        var isInvitationToGroup = chatRoom != null;
        if (isInvitationToGroup) {
            validateGroupRequest(sender, receiver, chatRoom);
        } else {
            validateFriendRequest(sender, receiver);
        }
    }

    public void sendInvitation(InvitationSendDto dto) {

        var chatRoomId = dto.chatGroupId();
        var receiverUsername = dto.receiverUserName();

        var sender = userService.getUserOrThrows();

        var receiver = getReceiverAndValidate(sender, receiverUsername);

        ChatRoom chatRoom = null;
        if (chatRoomId != null) {
            chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Chatroom with provided id not found."
                ));
        }

        validateChatRoomAndUsers(sender, receiver, chatRoom);

        var invitation = Invitation.builder()
            .status(Invitation.Status.PENDING)
            .sender(sender)
            .receiver(receiver)
            .chatRoom(chatRoom)
            .build();
        invitation = repository.saveAndFlush(invitation);

        Notification notification = Notification.builder()
                .title("Lời mời kết bạn mới")
                .content(sender.getUsername() + " đã gửi cho bạn lời mời kết bạn")
                .sender(sender)
                .receiver(receiver)
                .type(NotificationType.INVITATION)
                .build();
        notificationRepository.save(notification);

        notifyInvitation(invitation);
    }
}
