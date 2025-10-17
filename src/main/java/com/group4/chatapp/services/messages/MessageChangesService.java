package com.group4.chatapp.services.messages;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.AttachmentService;
import com.group4.chatapp.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class MessageChangesService {

    private final MessageRepository messageRepository;

    private final UserService userService;
    private final MessageCheckService checkService;
    private final AttachmentService attachmentService;

    private final SimpMessagingTemplate messagingTemplate;

    private void sendToMembers(ChatRoom chatRoom, ChatMessage savedMessage) {

        var socketPath = chatRoom.getSocketPath();
        var messageReceiveDto = new MessageReceiveDto(savedMessage);

        chatRoom.getMembers()
            .parallelStream()
            .forEach((member) -> {
                messagingTemplate.convertAndSendToUser(
                        member.getUsername(),
                        socketPath,
                        messageReceiveDto
                );

                if (chatRoom.getIsWaitingRoom()) {
                    messagingTemplate.convertAndSendToUser(
                            member.getUsername(),
                            "/queue/chat/waiting",
                            Map.of(
                                    "roomId", chatRoom.getId(),
                                    "message", messageReceiveDto
                            )
                    );
                } else {
                    messagingTemplate.convertAndSendToUser(
                            member.getUsername(),
                            "/queue/chat/main",
                            Map.of(
                                    "roomId", chatRoom.getId(),
                                    "message", messageReceiveDto
                            )
                    );
                }
            });
    }

    @Nullable
    private ChatMessage getReplyToAndCheck(ChatRoom chatRoom, MessageSendDto dto) {

        var replyToId = dto.getReplyTo();
        if (replyToId == null) {
            return null;
        }

        var message = messageRepository.findById(replyToId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "The reply to message not found!"
            ));

        var fromOtherRoom = !message.getRoom().equals(chatRoom);
        if (fromOtherRoom) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Can't reply to message from other room."
            );
        }

        return message;
    }

    private ChatMessage saveMessage(User user, ChatRoom chatRoom, MessageSendDto dto) {

        var attachments = attachmentService.saveFiles(dto.getAttachments());

        var newMessage = dto.toMessage(
            getReplyToAndCheck(chatRoom, dto),
            chatRoom, user, attachments,
            ChatMessage.Status.NORMAL
        );

        return messageRepository.save(newMessage);
    }

    @Transactional
    public MessageSendResponseDto sendMessage(long roomId, MessageSendDto dto) {

        var user = userService.getUserOrThrows();
        var chatRoom = checkService.receiveChatRoomAndCheck(roomId, user);

        var savedMessage = saveMessage(user, chatRoom, dto);
        sendToMembers(chatRoom, savedMessage);

        return new MessageSendResponseDto(savedMessage.getId());
    }

    @Transactional
    public void editMessage(long messageId, MessageSendDto dto) {

        var message = checkService.getMessageAndCheckSender(messageId);
        if (message.isRecalled()) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Can't edit recalled message."
            );
        }

        var attachments = attachmentService.saveFiles(dto.getAttachments());

        var chatRoom = message.getRoom();
        var sender = message.getSender();
        var now = new Timestamp(System.currentTimeMillis());

        var newMessage = dto.toMessage(
            getReplyToAndCheck(chatRoom, dto),
            chatRoom, sender, attachments,
            ChatMessage.Status.EDITED
        );

        newMessage.setId(message.getId());
        newMessage.setLastEdit(now);

        messageRepository.save(newMessage);

        // TODO: delete old resources
    }

    public void recallMessage(long messageId) {

        var message = checkService.getMessageAndCheckSender(messageId);
        var now = new Timestamp(System.currentTimeMillis());

        message.setMessage(null);
        message.setLastEdit(now);
        message.setAttachments(List.of());
        message.setStatus(ChatMessage.Status.RECALLED);

        messageRepository.save(message);
    }
}
