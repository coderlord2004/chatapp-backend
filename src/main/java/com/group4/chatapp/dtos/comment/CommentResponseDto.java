package com.group4.chatapp.dtos.comment;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private UserWithAvatarDto users;
    private Timestamp commentedAt;
    private CommentResponseDto parentComment;
    private List<CommentResponseDto> childComments;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.users = new UserWithAvatarDto(comment.getUser());
        this.commentedAt = comment.getCommentedAt();
        this.parentComment = new CommentResponseDto(comment.getParentComment());
    }

    public CommentResponseDto(Comment comment, List<CommentResponseDto> childComments) {
        this(comment);
        this.childComments = childComments;
    }
}
