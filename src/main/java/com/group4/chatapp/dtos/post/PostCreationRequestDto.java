package com.group4.chatapp.dtos.post;

import com.group4.chatapp.dtos.PostAttachmentDto;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreationRequestDto {
    private String caption;
    private int captionBackground;
    private String visibility;
    @Nullable
    private List<PostAttachmentDto> attachments;
}
