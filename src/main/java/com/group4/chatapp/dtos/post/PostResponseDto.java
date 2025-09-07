package com.group4.chatapp.dtos.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.PostAttachmentResponseDto;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {
    private Long id;
    private String caption;
    private Integer captionBackground;
    private PostVisibilityType visibility;
    private Timestamp createdOn;
    private Long totalReactions;
    private List<ReactionType> topReactionTypes;
    private Long totalComments;
    private Long totalShares;
    private List<PostAttachmentResponseDto> attachments;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.captionBackground = post.getCaptionBackground();
        this.visibility = post.getVisibility();
        this.createdOn = post.getCreatedOn();
        this.attachments = post.getPostAttachments()
                .stream()
                .map(PostAttachmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public PostResponseDto(Post post, Long totalReactions, List<ReactionType> reactionTypes, Long totalComments, Long totalShares) {
        this(post);
        this.totalReactions = totalReactions;
        this.topReactionTypes = reactionTypes;
        this.totalComments = totalComments;
        this.totalShares = totalShares;
    }
}
