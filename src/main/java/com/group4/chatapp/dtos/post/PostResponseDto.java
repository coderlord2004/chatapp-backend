package com.group4.chatapp.dtos.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.Enum.PostAttachmentType;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String caption;
    private Integer captionBackground;
    private PostVisibilityType visibility;
    private LocalDateTime publishedAt;
    private Long totalReactions;
    private List<ReactionType> topReactionTypes;
    private Long totalComments;
    private Long totalShares;
    private Long totalViews;
    private List<AttachmentDto> attachments;
    private PostResponseDto sharedPost;
    private PostAttachmentType postAttachmentType;
    private UserWithAvatarDto author;
    private ReactionType reacted;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.captionBackground = post.getCaptionBackground();
        this.visibility = post.getVisibility();
        this.publishedAt = post.getPublishedAt();
        this.totalReactions = post.getTotalReactions();
        this.topReactionTypes = new ArrayList<>();
        this.totalComments = post.getTotalComments();
        this.totalShares = post.getTotalShares();
        this.totalViews = post.getTotalViews();
        this.attachments = post.getAttachments().stream().map(AttachmentDto::new).toList();
        this.sharedPost = post.getSharedPost() != null ? new PostResponseDto(post.getSharedPost()) : null;
        this.postAttachmentType = post.getPostAttachmentType();
        this.author = new UserWithAvatarDto(post.getUser());
    }

    public PostResponseDto(Post post, List<ReactionType> topReactionTypes, ReactionType reacted) {
        this(post);
        this.topReactionTypes = topReactionTypes;
        this.reacted = reacted;
    }
}
