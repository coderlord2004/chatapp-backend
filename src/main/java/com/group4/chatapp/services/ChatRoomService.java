package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserService userService;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomDto getRoomWithLatestMessage(ChatRoom chatRoom) {

        var latestMessage = messageRepository
            .findLatestMessage(chatRoom.getId())
            .orElse(null);

        return new ChatRoomDto(chatRoom, latestMessage);
    }

    public List<ChatRoomDto> listRoomsWithLatestMessage() {
        var user = userService.getUserOrThrows();
        return chatRoomRepository.findWithLatestMessage(user.getId());
    }
}
