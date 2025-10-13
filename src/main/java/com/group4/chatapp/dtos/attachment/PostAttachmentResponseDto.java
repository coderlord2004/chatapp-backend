package com.group4.chatapp.dtos.attachment;

import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostAttachmentResponseDto {
    private Long postId;
    private AttachmentDto attachment;
    private Long totalReactions;
    private List<ReactionType> topReactionTypes;
    private Long totalComments;
    private Long totalShares;
    private Long totalViews;
    private ReactionType reacted;

    public PostAttachmentResponseDto(Long postId, Attachment attachment, List<ReactionType> topReactionTypes, ReactionType reacted) {
        this.postId = postId;
        this.attachment = new AttachmentDto(attachment);
        this.totalReactions = attachment.getTotalReactions();
        this.topReactionTypes = topReactionTypes;
        this.totalComments = attachment.getTotalComments();
        this.totalShares = attachment.getTotalShares();
        this.totalViews = attachment.getTotalViews();
        this.reacted = reacted;
    }
}
