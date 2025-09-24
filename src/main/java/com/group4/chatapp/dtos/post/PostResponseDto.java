package com.group4.chatapp.dtos.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.models.Enum.PostAttachmentType;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import com.group4.chatapp.models.Enum.ReactionType;

import java.sql.Timestamp;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponseDto(
        Long id,
        String caption,
        Integer captionBackground,
        PostVisibilityType visibility,
        Timestamp createdOn,
        Long totalReactions,
        List<ReactionType> topReactionTypes,
        Long totalComments,
        Long totalShares,
        List<AttachmentDto> attachments,
        PostResponseDto sharedPost,
        PostAttachmentType postAttachmentType
) {}