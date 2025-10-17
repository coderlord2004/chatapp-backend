package com.group4.chatapp.dtos.comment;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.Comment;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentDto {
    private Long commentId;
    private String content;
    private Timestamp commentedAt;
    private Long targetId;
    private TargetType targetType;
    private UserWithAvatarDto targetAuthor;

    public UserCommentDto (Comment comment, User targetAuthor) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.commentedAt = comment.getCommentedAt();
        this.targetId = comment.getTargetId();
        this.targetType = comment.getTargetType();
        this.targetAuthor = new UserWithAvatarDto(targetAuthor);
    }
}
