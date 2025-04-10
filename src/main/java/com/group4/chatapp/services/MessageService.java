package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private RuntimeException chatRoomNotFoundException() {
        return new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "chatRoom with provided id not found!"
        );
    }

    private void sendToMembers(User sender, ChatRoom chatRoom, String message) {

        var socketPath = chatRoom.getSocketPath();
        var messageReceiveDto = new MessageReceiveDto(
            chatRoom.getId(),
            sender.getUsername(),
            message
        );

        chatRoom.getMembers()
            .parallelStream()
            .filter((member) -> !sender.equals(member))
            .forEach((member) ->
                messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    socketPath,
                    messageReceiveDto
                )
            );
    }

    private void saveMessage(User user, ChatRoom chatRoom, String message) {

        var newMessage = ChatMessage.builder()
            .room(chatRoom)
            .sender(user)
            .message(message)
            .attachments(List.of()) // TODO: handle message with attachments
            .build();

        messageRepository.save(newMessage);
    }

    @Transactional
    public void sendMessage(long roomId, MessageSendDto dto) {

        var chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(this::chatRoomNotFoundException);

        var user = userService.getUserByContext().orElse(null);
        if (user == null || !user.inChatRoom(chatRoom)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You aren't in this room!"
            );
        }

        // TODO: can be run asynchronously
        saveMessage(user, chatRoom, dto.message());
        sendToMembers(user, chatRoom, dto.message());
    }
}
