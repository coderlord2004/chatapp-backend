package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.repositories.UserRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class InvitationSendService {

    private final UserService userService;

    private final UserRepository userRepository;
    private final InvitationRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    private final SimpMessagingTemplate messagingTemplate;

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

        return userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Receiver with provided username not found!"
            ));
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
                "You sent the same invitation before"
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
        notifyInvitation(invitation);
    }
}
