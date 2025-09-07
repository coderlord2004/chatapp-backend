package com.group4.chatapp.dtos;

import com.group4.chatapp.models.PostAttachment.PostAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAttachmentResponseDto {
    private String description;
    private String attachmentUrl;
    private String attachmentType;

    public PostAttachmentResponseDto(PostAttachment postAttachment) {
        this.description = postAttachment.getDescription();
        this.attachmentType = String.valueOf(postAttachment.getAttachment().getType());
        this.attachmentUrl = postAttachment.getAttachment().getSource();
    }
}
