package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.callInvitation.CallInvitationResponseDto;
import com.group4.chatapp.dtos.callInvitation.CallInvitationSendDto;
import com.group4.chatapp.dtos.callInvitation.CancelCallInvitationDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.services.messages.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallService {
    SimpMessagingTemplate messagingTemplate;
    UserService userService;
    MessageService messageService;
    ChatRoomRepository chatRoomRepository;

    public void sendInvitationToChannel (CallInvitationSendDto dto) {
        User authUser = userService.getUserOrThrows();
        List<UserWithAvatarDto> members = chatRoomRepository.findChatRoomWithUsername(dto.getChannelId());

        CallInvitationResponseDto callInvitationResponseDto = CallInvitationResponseDto.builder()
                .channelId(dto.getChannelId())
                .caller(new UserWithAvatarDto(authUser))
                .members(members)
                .isUseVideo(dto.getIsUseVideo())
                .build();

        members.forEach(user -> {
            if (!user.getUsername().equals(authUser.getUsername())) {
                messagingTemplate.convertAndSendToUser(
                        user.getUsername(),
                        "/queue/send_call_invitation",
                        callInvitationResponseDto
                );
            }
        });
    }

    public void cancelCallInvitation (CancelCallInvitationDto dto) {
        User authUser = userService.getUserOrThrows();
        List<UserWithAvatarDto> members = chatRoomRepository.findChatRoomWithUsername(dto.getChannelId());

        members.forEach(user -> {
            if (!user.getUsername().equals(authUser.getUsername())) {
                messagingTemplate.convertAndSendToUser(
                        user.getUsername(),
                        "/queue/cancel_call_invitation",
                        Map.of("sender", authUser.getUsername())
                );
            }
        });
    }

    public void refuseCallInvitation (String caller) {
        User authUser = userService.getUserOrThrows();
        Map<String, Object> response = Map.of(
                "sender", authUser.getUsername()
        );

        messagingTemplate.convertAndSendToUser(
                caller,
                "/queue/refuse_call_invitation",
                response
        );
    }
}
