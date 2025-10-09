package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.invitation.InvitationWithNewRoomDto;
import com.group4.chatapp.dtos.invitation.ReplyResponse;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.UserRelation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.UserRelationRepository;
import com.group4.chatapp.services.ChatRoomService;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
class InvitationReplyService {

    private final UserService userService;
    private final ChatRoomService chatRoomService;

    private final UserRelationRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private void updateInvitation(UserRelation userRelation, boolean isAccepted) {

        var status = isAccepted
            ? UserRelation.Status.ACCEPTED
            : UserRelation.Status.REJECTED;

        userRelation.setStatus(status);
        repository.saveAndFlush(userRelation);
    }

    private ChatRoom createDuoChatRoom(UserRelation userRelation) {

        var members = Set.of(
            userRelation.getSender(),
            userRelation.getReceiver()
        );

        var newChatRoom = ChatRoom.builder()
            .type(ChatRoom.Type.DUO)
            .members(members)
            .build();

        return chatRoomRepository.save(newChatRoom);
    }

    private ChatRoom createChatGroup(Set<User> members) {

        var newGroupChat = ChatRoom.builder()
            .type(ChatRoom.Type.GROUP)
            .members(members)
            .build();

        return chatRoomRepository.save(newGroupChat);
    }

    private void notifyUserReply(
        UserRelation userRelation,
        @Nullable ChatRoomDto newChatRoomDto
    ) {

        var isFriendRequest = userRelation.getChatRoom() != null;
        var needSendToMembers = isFriendRequest && userRelation.isAccepted();

        var receiver = new ArrayList<User>();

        if (needSendToMembers) {
            receiver.addAll(userRelation.getChatRoom().getMembers());
        } else {
            receiver.add(userRelation.getSender());
        }

        var sendObject = new InvitationWithNewRoomDto(userRelation, newChatRoomDto);

        receiver.parallelStream()
            .forEach(user ->
                messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/invitationReplies/",
                    sendObject
                )
            );
    }

    private ChatRoom getNewChatRoom(UserRelation userRelation) {

        if (userRelation.isFriendRequest()) {
            return createDuoChatRoom(userRelation);
        }

        var chatRoom = userRelation.getChatRoom();
        var receiver = userRelation.getReceiver();

        assert chatRoom != null;

        if (chatRoom.isChatGroup()) {
            chatRoom.getMembers().add(receiver);
            return chatRoomRepository.save(chatRoom);
        }

        var members = new HashSet<>(chatRoom.getMembers());
        members.add(receiver);

        return createChatGroup(members);
    }

    private UserRelation getInvitationAndCheck(User user, long invitationId) {

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

        return invitation;
    }

    public ReplyResponse replyInvitation(long invitationId, boolean isAccepted) {

        var user = userService.getUserOrThrows();
        var invitation = getInvitationAndCheck(user, invitationId);

        ChatRoomDto newChatRoomDto = null;

        if (isAccepted) {
            var newChatRoom = getNewChatRoom(invitation);
            newChatRoomDto = chatRoomService.getRoomWithLatestMessage(newChatRoom);
        }

        updateInvitation(invitation, isAccepted);
        notifyUserReply(invitation, newChatRoomDto);

        return new ReplyResponse(newChatRoomDto);
    }
}
