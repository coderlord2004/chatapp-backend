package com.group4.chatapp.services.messages;

import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.ChatRoomService;
import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageCheckService {

    private final UserService userService;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    public ChatRoom receiveChatRoomAndCheck(long id, User user) {

        var chatRoom = chatRoomService.getChatRoom(id);
        if (chatRoom.getLeaderOnlySend()) {
            if (!Objects.equals(chatRoom.getLeader(), user)) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Only chat room's leader can do this!"
                );
            }
        }
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
