package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.CreateChatRoomDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${messages.max-request}")
    private int messageRequestSize;

    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "Chat room is not found!"
        ));
    }

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

    @Transactional(readOnly = true)
    public ChatRoomDto getChatRoomByUsername(String username) {
        var user = userService.getUserOrThrows();
        User otherUser = userService.getUserByUsername(username);
        ChatRoom chatRoom = chatRoomRepository.findDuoChatRoom(user.getId(), otherUser.getId());
        if (chatRoom == null) {
            ChatRoom waitingRoom = ChatRoom.builder()
                    .name(user.getUsername() + ", " + otherUser.getUsername())
                    .members(new HashSet<>(List.of(user, otherUser)))
                    .type(ChatRoom.Type.DUO)
                    .isWaitingRoom(true)
                    .build();
            return new ChatRoomDto(chatRoomRepository.save(waitingRoom));
        }
        List<ChatMessage> chatMessageStream = messageRepository.findByRoomId(
                chatRoom.getId(),
                PageRequest.of(0, messageRequestSize,
                Sort.by(Sort.Direction.ASC, "sentOn")
        ));

        return new ChatRoomDto(chatRoom, chatMessageStream);
    }

    public List<ChatRoomDto> getWaitingRooms() {
        User authUser = userService.getUserOrThrows();

        return chatRoomRepository.findWaitingRoom(authUser.getId());
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
                .leader(authUser)
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

        return new ChatRoomDto(chatRoom, (ChatMessage) null);
    }

    public void updatePermission(Long roomId) {
        User authUser = userService.getUserOrThrows();
        ChatRoom chatRoom = getChatRoom(roomId);
        if (!Objects.equals(chatRoom.getLeader().getId(), authUser.getId())) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Only leader can do this!"
            );
        }

        chatRoom.setLeaderOnlySend(!chatRoom.getLeaderOnlySend());
        chatRoomRepository.save(chatRoom);
    }
}
