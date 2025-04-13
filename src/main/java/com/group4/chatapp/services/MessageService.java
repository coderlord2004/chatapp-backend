package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.exceptions.ChatRoomNotFoundException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
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

    private void sendToMembers(ChatRoom chatRoom, ChatMessage savedMessage) {

        var sender = savedMessage.getSender();
        var socketPath = chatRoom.getSocketPath();

        var messageReceiveDto = new MessageReceiveDto(savedMessage);

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

    private ChatMessage saveMessage(User user, ChatRoom chatRoom, MessageSendDto dto) {
        var newMessage = dto.toMessage(chatRoom, user);
        return messageRepository.save(newMessage);
    }

    private ChatRoom receiveChatRoomAndCheck(long id, @Nullable User user) {

        var chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(ChatRoomNotFoundException::new);

        if (user == null || !user.inChatRoom(chatRoom)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You aren't in this room!"
            );
        }

        return chatRoom;
    }

    @SuppressWarnings("UnusedReturnValue")
    private ChatRoom receiveChatRoomAndCheck(long id) {
        var user = userService.getUserByContext().orElse(null);
        return receiveChatRoomAndCheck(id, user);
    }

    @Transactional
    public void sendMessage(long roomId, MessageSendDto dto) {

        var user = userService.getUserByContext().orElse(null);
        var chatRoom = receiveChatRoomAndCheck(roomId, user);

        var savedMessage = saveMessage(user, chatRoom, dto);
        sendToMembers(chatRoom, savedMessage);
    }

    @Transactional
    public List<MessageReceiveDto> getMessages(long roomId) {

        receiveChatRoomAndCheck(roomId);

        return messageRepository.findByRoomId(roomId)
            .map(MessageReceiveDto::new)
            .toList();
    }
}
