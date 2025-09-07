package com.group4.chatapp.services;

import com.group4.chatapp.dtos.comment.CommentCreationDto;
import com.group4.chatapp.dtos.comment.CommentResponseDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Comment;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CommentService {
    private CommentRepository commentRepository;
    private UserService userService;

    public void createComment(CommentCreationDto dto) {
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
        } else {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "You only comment three times in one the post."
            );
        }
    }

    public List<CommentResponseDto> getComments(Long targetId, TargetType targetType) {
        List<Comment> comments = commentRepository.getComments(targetId, targetType);
        return comments.stream().map(CommentResponseDto::new).toList();
    }
}
