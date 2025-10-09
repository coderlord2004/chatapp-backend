package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.invitation.UserRelationDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.dtos.invitation.ReplyResponse;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.models.UserRelation;
import com.group4.chatapp.repositories.UserRelationRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserRelationService {

    private final UserRelationRepository repository;

    private final UserService userService;
    private final InvitationSendService sendService;
    private final InvitationReplyService replyService;

    @Transactional(readOnly = true)
    public List<UserRelationDto> getInvitations() {

        var user = userService.getUserOrThrows();

        return repository.findByReceiverId(user.getId())
            .map(UserRelationDto::new)
            .toList();
    }

    @Transactional
    public void sendInvitation(InvitationSendDto dto) {
        sendService.sendInvitation(dto);
    }

    @Transactional
    public ReplyResponse replyInvitation(long invitationId, boolean isAccepted) {
        return replyService.replyInvitation(invitationId, isAccepted);
    }
}
