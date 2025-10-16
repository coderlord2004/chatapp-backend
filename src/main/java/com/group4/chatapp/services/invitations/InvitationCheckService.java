package com.group4.chatapp.services.invitations;

import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvitationCheckService {
    private final InvitationRepository repository;

    public Invitation checkSenderPermission(User authUser, User otherUser) {
        Invitation invitation = repository.findBySenderIdAndReceiverId(authUser.getId(), otherUser.getId());
        if (invitation == null) return null;

        if (invitation.isBlock()) {
            if (Objects.equals(authUser, invitation.getSender())) {
                throw new ApiException(
                        HttpStatus.FORBIDDEN,
                        "You are blocking " + otherUser.getUsername() + "!"
                );
            } else {
                throw new ApiException(
                        HttpStatus.FORBIDDEN,
                        otherUser.getUsername() + " are blocking you!"
                );
            }
        } else {
            return invitation;
        }
    }
}
