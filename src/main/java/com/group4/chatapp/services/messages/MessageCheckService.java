package com.group4.chatapp.services.messages;

import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.ChatRoomService;
import com.group4.chatapp.services.UserService;
import com.group4.chatapp.services.invitations.UserRelationCheckService;
import com.group4.chatapp.services.invitations.UserRelationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class MessageCheckService {
    private UserService userService;
    private MessageRepository messageRepository;
    private ChatRoomRepository chatRoomRepository;
    private ChatRoomService chatRoomService;
    private UserRelationService userRelationService;
    private UserRelationCheckService userRelationCheckService;

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
        if (chatRoom.isChatDuo()) {
            List<User> members = chatRoom.getMembers().stream().toList();
            User authUser = members.get(0);
            User otherUser = members.get(1);
            userRelationCheckService.checkSenderPermission(authUser, otherUser);
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
