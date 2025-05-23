package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.AttachmentRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private final CloudinaryService cloudinaryService;

    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

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

    private List<Attachment> getAttachments(MessageSendDto dto) {

        List<Map<String, ?>> uploadedFiles;
        try {
            uploadedFiles = cloudinaryService.uploadMutiFile(dto.getAttachments());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (uploadedFiles == null) {
            return List.of();
        }

        return uploadedFiles
            .stream()
            .map((file) -> {

                if (!file.get("status").equals("success")) {
                    return null;
                }

                var resourceType = (String) file.get("resource_type");
                var format = (String) file.get("format");

                var attachment = Attachment.builder()
                    .source((String) file.get("secure_url"))
                    .type(attachmentService.checkTypeInFileType(resourceType, format))
                    .build();

                return attachmentRepository.save(attachment);
            })
            .toList();
    }

    private ChatMessage saveMessage(User user, ChatRoom chatRoom, MessageSendDto dto) {

        var attachments = getAttachments(dto);
        var newMessage = dto.toMessage(chatRoom, user, attachments);

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

        if (dto.getMessage().isEmpty() && dto.getAttachments() == null) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Message and files are not empty!"
            );
        }

        var user = userService.getUserOrThrows();
        var chatRoom = receiveChatRoomAndCheck(roomId, user);

        var savedMessage = saveMessage(user, chatRoom, dto);
        sendToMembers(chatRoom, savedMessage);
    }

    @Transactional
    public List<MessageReceiveDto> getMessages(long roomId, int page) {

        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Page isn't less than 1!"
            );
        }

        receiveChatRoomAndCheck(roomId);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            50,
            Sort.by(Sort.Direction.DESC, "sentOn")
        );

        var messages = messageRepository.findByRoomId(roomId, pageRequest)
            .map(MessageReceiveDto::new)
            .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }
}
