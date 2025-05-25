package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageSendDto {

    @Nullable
    private Long replyTo;

    @Nullable
    private String message;

    @Nullable
    private List<MultipartFile> attachments;

    public ChatMessage toMessage(
        @Nullable ChatMessage replyTo,
        ChatRoom room,
        User sender,
        List<Attachment> attachments,
        ChatMessage.Status status
    ) {

        return ChatMessage.builder()
            .replyTo(replyTo)
            .room(room)
            .sender(sender)
            .message(this.message)
            .status(status)
            .attachments(attachments)
            .build();
    }

    public void validate() {
        if (
            CollectionUtils.isEmpty(attachments)
                && StringUtils.isEmpty(message)
        ) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "message and attachment mustn't be empty together!"
            );
        }
    }
}
