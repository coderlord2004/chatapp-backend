package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
class InvitationReplyService {

    private final UserService userService;

    private final InvitationRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    private final SimpMessagingTemplate messagingTemplate;

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
