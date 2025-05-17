package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private void sendToMembers(ChatRoom chatRoom, ChatMessage savedMessage) {

        var socketPath = chatRoom.getSocketPath();

        var messageReceiveDto = new MessageReceiveDto(savedMessage);

        chatRoom.getMembers()
            .parallelStream()
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

    private ChatRoom receiveChatRoomAndCheck(long id, User user) {

        var chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chatroom with provided id not found"
            ));

        var userInChatRoom = chatRoomRepository.userIsMemberInChatRoom(user.getId(), id);
        if (!userInChatRoom) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You aren't in this room!"
            );
        }

        return chatRoom;
    }

    @SuppressWarnings("UnusedReturnValue")
    private ChatRoom receiveChatRoomAndCheck(long id) {
        var user = userService.getUserOrThrows();
        return receiveChatRoomAndCheck(id, user);
    }

    @Transactional
    public void sendMessage(long roomId, MessageSendDto dto) {

        var user = userService.getUserOrThrows();
        var chatRoom = receiveChatRoomAndCheck(roomId, user);

        var savedMessage = saveMessage(user, chatRoom, dto);
        sendToMembers(chatRoom, savedMessage);
    }

    @Transactional
    public List<MessageReceiveDto> getMessages(long roomId, int page) {
        if (page < 1)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page isn't less than 1!"
            );

        receiveChatRoomAndCheck(roomId); // logic kiểm tra quyền phòng

        PageRequest pageRequest = PageRequest.of(page - 1, 50, Sort.by(Sort.Direction.DESC, "sentOn"));

        Stream<ChatMessage> stream = messageRepository.findByRoomId(roomId, pageRequest);
        return stream
                .map(MessageReceiveDto::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list;
                }));
    }
}
