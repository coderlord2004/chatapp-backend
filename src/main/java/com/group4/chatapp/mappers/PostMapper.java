package com.group4.chatapp.mappers;

import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface PostMapper {
    @Mapping(target = "topReactionTypes", ignore = true)
    PostResponseDto toDto(Post post);

    List<PostResponseDto> toDtoList(List<Post> posts);

    default PostResponseDto toDto(Post post, List<ReactionType> topReactionTypes) {
        PostResponseDto dto = toDto(post);
        return new PostResponseDto(
                dto.id(),
                dto.caption(),
                dto.captionBackground(),
                dto.visibility(),
                dto.createdOn(),
                dto.totalReactions(),
                topReactionTypes,
                dto.totalComments(),
                dto.totalShares(),
                dto.attachments(),
                dto.sharedPost(),
                dto.postAttachmentType()
        );
    }
}
