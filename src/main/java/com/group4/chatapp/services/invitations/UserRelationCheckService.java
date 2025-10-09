package com.group4.chatapp.services.invitations;

import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.models.UserRelation;
import com.group4.chatapp.repositories.UserRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRelationCheckService {
    private final UserRelationRepository repository;

    public void checkSenderPermission(User authUser, User otherUser) {
        UserRelation userRelation = repository.getUserRelation(authUser.getId(), otherUser.getId());
        if (userRelation == null) return;

        if (userRelation.getIsBlocking()) {
            if (Objects.equals(authUser, userRelation.getSender())) {
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
        }
    }
}
