package com.group4.chatapp.services;

import com.group4.chatapp.dtos.callInvitation.CallInvitationResponseDto;
import com.group4.chatapp.dtos.callInvitation.CallInvitationSendDto;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.services.messages.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallService {
    SimpMessagingTemplate messagingTemplate;
    UserService userService;
    MessageService messageService;

    public void sendInvitationToChannel (CallInvitationSendDto dto) {
        User authUser = userService.getUserOrThrows();
        ChatRoom chatRoom = messageService.getCheckService().receiveChatRoomAndCheck(dto.getChannelId());

        assert chatRoom.getName() != null && chatRoom.getAvatar() != null;

        CallInvitationResponseDto callInvitationResponseDto = CallInvitationResponseDto.builder()
                .agoraToken(dto.getAgoraToken())
                .isUseVideo(dto.getIsUseVideo())
                .chatRoomDto(Map.of(
                        "id", chatRoom.getId(),
                        "name", chatRoom.getName(),
                        "avatar", chatRoom.getAvatar(),
                        "type", chatRoom.getType()
                ))
                .build();

        dto.getMembersUsername().forEach(username -> {
            if (!username.equals(authUser.getUsername())) {
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/call_invitation",
                        callInvitationResponseDto
                );
            }
        });
    }

    public void cancelCallInvitation () {

    }
}
