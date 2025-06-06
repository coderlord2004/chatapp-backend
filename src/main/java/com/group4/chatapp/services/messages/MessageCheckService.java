package com.group4.chatapp.services.messages;

import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
class MessageCheckService {

    private final UserService userService;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom receiveChatRoomAndCheck(long id, User user) {

        var chatRoom = chatRoomRepository.findById(id)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chatroom with provided id not found!"
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
    public ChatRoom receiveChatRoomAndCheck(long id) {
        var user = userService.getUserOrThrows();
        return receiveChatRoomAndCheck(id, user);
    }

    public ChatMessage getMessageAndCheckSender(long id) {

        var user = userService.getUserOrThrows();
        var message = messageRepository.findById(id)
            .orElseThrow(() ->
                new ApiException(
                    HttpStatus.NOT_FOUND,
                    "The old message not found."
                )
            );

        var isSender = message.getSender().equals(user);
        if (!isSender) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not the sender of the message"
            );
        }

        return message;
    }
}
