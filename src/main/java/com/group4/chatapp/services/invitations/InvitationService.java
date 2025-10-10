package com.group4.chatapp.services.invitations;

import com.group4.chatapp.dtos.invitation.InvitationDto;
import com.group4.chatapp.dtos.invitation.InvitationSendDto;
import com.group4.chatapp.dtos.invitation.ReplyResponse;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository repository;

    private final UserService userService;
    private final InvitationSendService sendService;
    private final InvitationReplyService replyService;

    @Transactional(readOnly = true)
    public List<InvitationDto> getInvitations() {

        var user = userService.getUserOrThrows();

        return repository.findByReceiverId(user.getId())
            .map(InvitationDto::new)
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
