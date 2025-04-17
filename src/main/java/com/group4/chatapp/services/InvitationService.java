package com.group4.chatapp.services;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserService userService;
    private final UserRepository userRepository;

    private final InvitationRepository repository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = true)
    public List<InvitationDto> getInvitations() {

        var user = userService.getUserOrThrows();

        return repository.findByReceiverId(user.getId())
            .map(InvitationDto::new)
            .toList();
    }

    @Transactional
    public void sendInvitation(String receiverUsername) {

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

        var invitation = Invitation.builder()
            .sender(sender)
            .receiver(receiver)
            .status(Invitation.Status.PENDING)
            .build();

        repository.save(invitation);

        // TODO: send invitation to receiver websocket
    }

    private void checkInvitation(Invitation invitation, User user) {

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
    }

    public void updateInvitation(Invitation invitation, boolean isAccepted) {

        var status = isAccepted
            ? Invitation.Status.ACCEPTED
            : Invitation.Status.REJECTED;

        invitation.setStatus(status);
        repository.save(invitation);
    }

    public void createDuoChatRoom(Invitation invitation) {

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

    @Transactional
    public void replyInvitation(long invitationId, boolean isAccepted) {

        var user = userService.getUserOrThrows();

        var invitation = repository.findById(invitationId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Invitation with provided id not found!"
            ));

        checkInvitation(invitation, user);
        updateInvitation(invitation, isAccepted);

        if (isAccepted) {
            createDuoChatRoom(invitation);
        }

        // TODO: send message to sender's websocket with reply status
    }
}
