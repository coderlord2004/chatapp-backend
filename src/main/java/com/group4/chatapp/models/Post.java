package com.group4.chatapp.models;

import com.group4.chatapp.models.Enum.PostAttachmentType;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post extends Content {
    @Lob
    private String caption;

    @Column(name = "caption_background")
    private Integer captionBackground;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostVisibilityType visibility = PostVisibilityType.PUBLIC;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "post_attachment_type")
    @Builder.Default
    private PostAttachmentType postAttachmentType = PostAttachmentType.MEDIA;

    @Enumerated(value = EnumType.STRING)
    private PostStatus status;

    @CreationTimestamp
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shared_post_id")
    private Post sharedPost;

    public enum PostStatus {
        PUBLISHED,
        DRAFT
    }
}
