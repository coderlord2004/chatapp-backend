package com.group4.chatapp.services;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserService userService;
    private final UserRepository userRepository;

    private final InvitationRepository repository;

    @Transactional
    public List<InvitationDto> getInvitations() {

        var user = userService.getUserOrThrows();

        return repository.findByReceiverId(user.getId())
            .map(InvitationDto::new)
            .toList();
    }

    @Transactional
    public void sendInvitation(String receiverUsername) {

        var sender = userService.getUserOrThrows();

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

    @Transactional
    public void acceptInvitation(long invitationId) {

        var user = userService.getUserOrThrows();

        var invitation = repository.findById(invitationId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Invitation with provided id not found!"
            ));

        var userIsReceiver = invitation.getReceiver().equals(user);
        if (!userIsReceiver) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "You aren't the receiver of the invitation!"
            );
        }

        if (!invitation.isPending()) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Invitation has been replied."
            );
        }

        invitation.setStatus(Invitation.Status.ACCEPTED);
        repository.save(invitation);

        // TODO: create new chatroom
        // TODO: send message to sender's websocket with reply status
    }
}
