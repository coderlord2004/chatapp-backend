package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.CreateChatRoomDto;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    public ChatRoomDto createChatRoom (CreateChatRoomDto dto) {
        var authUser = userService.getUserOrThrows();

        Set<User> members = new HashSet<>();
        members.add(authUser);

        dto.getMembers().forEach(username -> {
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException(username + " not found!"));
            members.add(user);
        });

        ChatRoom chatRoom = ChatRoom.builder()
                .name(dto.getChatRoomName())
                .type(ChatRoom.Type.GROUP)
                .members(members)
                .build();
        chatRoom = chatRoomRepository.save(chatRoom);

        for (String username : dto.getMembers()) {
            simpMessagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/chatroom/create",
                    Map.of(
                            "sender", authUser.getUsername(),
                            "chatRoom", chatRoom
                    )
            );
        }

        return new ChatRoomDto(chatRoom, null);
    }
}
