package com.group4.chatapp.dtos.attachment;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SharedAttachmentDto {
    private Long id;
    private String caption;
    private Integer captionBackground;
    private PostVisibilityType visibility;
    private LocalDateTime publishedAt;
    private UserWithAvatarDto author;
    
    private AttachmentDto attachment;

    public SharedAttachmentDto(Post post, Attachment attachment) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.captionBackground = post.getCaptionBackground();
        this.visibility = post.getVisibility();
        this.publishedAt = post.getPublishedAt();   
        this.author = new UserWithAvatarDto(post.getUser());
        this.attachment = new AttachmentDto(attachment);
    }
}
