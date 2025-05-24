package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendDto {

    private String message;

    private List<MultipartFile> attachments;

    public ChatMessage toMessage(ChatRoom room, User sender, List<Attachment> attachments) {
        return ChatMessage.builder()
                .room(room)
                .sender(sender)
                .message(this.message)
                .attachments(attachments)
                .build();
    }
}
