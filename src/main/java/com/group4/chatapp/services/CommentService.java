package com.group4.chatapp.services;

import com.group4.chatapp.dtos.comment.CommentRequestDto;
import com.group4.chatapp.dtos.comment.CommentResponseDto;
import com.group4.chatapp.dtos.comment.UpdateCommentDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Comment;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.CommentRepository;
import com.group4.chatapp.repositories.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CommentService {
    private CommentRepository commentRepository;
    private UserService userService;
    private ContentRepository contentRepository;

    public void createComment(CommentRequestDto dto) {
        User authUser = userService.getUserOrThrows();
        Long totalCommentByUser = commentRepository.countByUserId(authUser.getId());
        if (totalCommentByUser < 3) {
            Comment comment = Comment.builder()
                    .user(authUser)
                    .content(dto.getContent())
                    .targetId(dto.getTargetId())
                    .targetType(dto.getTargetType())
                    .build();

            commentRepository.save(comment);
            contentRepository.increaseComments(dto.getTargetId());
        } else {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "You only comment three times in one the post."
            );
        }
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new ApiException(
                HttpStatus.BAD_REQUEST,
                "Comment is not found!"
        ));
    }

    public List<CommentResponseDto> getComments(Long targetId, TargetType targetType) {
        List<Comment> comments = commentRepository.getComments(targetId, targetType);
        return comments.stream().map(CommentResponseDto::new).toList();
    }

    public void updateComment(UpdateCommentDto dto) {
        Comment comment = getComment(dto.getCommentId());
        comment.setContent(dto.getContent());
        comment.setCommentedAt(Timestamp.valueOf(LocalDateTime.now()));
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        contentRepository.decreaseComments(comment.getTargetId());
        commentRepository.deleteById(commentId);
    }
}
