package com.group4.chatapp.models.PostAttachment;

import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_attachment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAttachment {
    @EmbeddedId
    private PostAttachmentID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attachmentId")
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @Column(columnDefinition = "TEXT")
    private String description;
}
