package com.group4.chatapp.services.messages;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageChangesService sendService;
    private final MessageCheckService checkService;
    private final MessageRepository messageRepository;

    @Transactional
    public List<MessageReceiveDto> getMessages(long roomId, int page) {

        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Page mustn't less than 1!"
            );
        }

        checkService.receiveChatRoomAndCheck(roomId);

        var pageRequest = PageRequest.of(
            page - 1, 50,
            Sort.by(Sort.Direction.DESC, "sentOn")
        );

        var messages = messageRepository.findByRoomId(roomId, pageRequest)
            .map(MessageReceiveDto::new)
            .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    public MessageSendResponseDto sendMessage(long roomId, MessageSendDto dto) {
        return sendService.sendMessage(roomId, dto);
    }

    public void changeMessage(long messageId, MessageSendDto dto) {
         sendService.editMessage(messageId, dto);
    }

    public void deleteMessage(long messageId) {
        sendService.recallMessage(messageId);
    }
}
