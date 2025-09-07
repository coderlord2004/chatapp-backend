package com.group4.chatapp.models;

import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.PostAttachment.PostAttachment;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String caption;

    @Column(name = "caption_background")
    @Builder.Default
    private Integer captionBackground = null;

    @CreationTimestamp
    @Column(name = "created_on")
    private Timestamp createdOn;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostVisibilityType visibility = PostVisibilityType.PUBLIC;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostAttachment> postAttachments;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
