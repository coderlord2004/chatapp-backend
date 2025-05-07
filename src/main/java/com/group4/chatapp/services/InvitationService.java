package com.group4.chatapp.services;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserService userService;
    private final UserRepository userRepository;

    private final InvitationRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private boolean ableToSendInvitation(User user, @Nullable Long roomId) {
        if (roomId == null) return true;
        return chatRoomRepository.userIsMemberInChatRoom(user.getId(), roomId);
    }

    private void updateInvitation(Invitation invitation, boolean isAccepted) {

        var status = isAccepted
            ? Invitation.Status.ACCEPTED
            : Invitation.Status.REJECTED;

        invitation.setStatus(status);
        repository.saveAndFlush(invitation);
    }

    private void createDuoChatRoom(Invitation invitation) {

        var members = Set.of(
            invitation.getSender(),
            invitation.getReceiver()
        );

        var newChatRoom = ChatRoom.builder()
            .type(ChatRoom.Type.DUO)
            .members(members)
            .build();

        chatRoomRepository.save(newChatRoom);
    }

    private void addUserToChatroom(ChatRoom chatRoom, User user) {
        chatRoom.getMembers().add(user);
        chatRoomRepository.save(chatRoom);
    }

    private void createChatGroup(Set<User> members) {

        var newGroupChat = ChatRoom.builder()
            .type(ChatRoom.Type.GROUP)
            .members(members)
            .build();

        chatRoomRepository.save(newGroupChat);
    }

    private void notifyUserReply(Invitation invitation) {

        var receiver = new ArrayList<User>();
        var needSendToMembers = invitation.getChatRoom() != null
            && invitation.getStatus() != Invitation.Status.ACCEPTED;

        if (needSendToMembers) {
            receiver.addAll(invitation.getChatRoom().getMembers());
        } else {
            receiver.add(invitation.getSender());
        }

        receiver.parallelStream()
            .map(User::getUsername)
            .forEach(username ->
                messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/invitationReplies/",
                    new InvitationDto(invitation)
                )
            );
    }

    private void notifyInvitation(Invitation invitation) {
        messagingTemplate.convertAndSendToUser(
            invitation.getReceiver().getUsername(),
            "/queue/invitations/",
            new InvitationDto(invitation)
        );
    }

    private void onInvitationAccepted(Invitation invitation) {

        if (invitation.isFriendRequest()) {
            createDuoChatRoom(invitation);
            return;
        }

        var chatRoom = invitation.getChatRoom();
        var receiver = invitation.getReceiver();

        assert chatRoom != null;

        if (chatRoom.isChatGroup()) {
            addUserToChatroom(chatRoom, receiver);
            return;
        }

        var members = new HashSet<>(chatRoom.getMembers());
        members.add(receiver);

        createChatGroup(members);
    }

    @Transactional(readOnly = true)
    public List<InvitationDto> getInvitations() {

        var user = userService.getUserOrThrows();

        return repository.findByReceiverId(user.getId())
            .map(InvitationDto::new)
            .toList();
    }

    @Transactional
    public void sendInvitation(InvitationSendDto dto) {

        var chatRoomId = dto.chatGroupId();
        var receiverUsername = dto.receiverUserName();

        var sender = userService.getUserOrThrows();
        var senderIsReceiver = sender.getUsername().equals(receiverUsername);

        if (senderIsReceiver) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You are the receiver of the message!"
            );
        }

        var receiver = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Receiver with provided username not found!"
            ));

        var areFriends = chatRoomRepository.areUsersHasRoomOfType(
            sender.getId(),
            receiver.getId(),
            ChatRoom.Type.DUO
        );

        if (areFriends) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You and the receiver are friends."
            );
        }

        if (!ableToSendInvitation(sender, chatRoomId)) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You aren't in this Room."
            );
        }

        ChatRoom chatRoom = null;
        if (chatRoomId != null) {

            var receiverInChatRoom = chatRoomRepository.userIsMemberInChatRoom(
                receiver.getId(), chatRoomId
            );

            if (receiverInChatRoom) {
                throw new ApiException(
                    HttpStatus.CONFLICT,
                    "The receiver are in this room."
                );
            }

            chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Chatroom with provided id not found"
                ));
        }

        var invitation = Invitation.builder()
            .status(Invitation.Status.PENDING)
            .sender(sender)
            .receiver(receiver)
            .chatRoom(chatRoom)
            .build();

        invitation = repository.saveAndFlush(invitation);
        notifyInvitation(invitation);
    }

    @Transactional
    public void replyInvitation(long invitationId, boolean isAccepted) {

        var user = userService.getUserOrThrows();

        var invitation = repository.findById(invitationId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Invitation with provided id not found!"
            ));

        var userIsReceiver = invitation.getReceiver().equals(user);
        if (!userIsReceiver) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You aren't the receiver of the invitation!"
            );
        }

        if (!invitation.isPending()) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Invitation has been replied."
            );
        }

        if (isAccepted) {
            onInvitationAccepted(invitation);
        }

        updateInvitation(invitation, isAccepted);
        notifyUserReply(invitation);
    }
}
