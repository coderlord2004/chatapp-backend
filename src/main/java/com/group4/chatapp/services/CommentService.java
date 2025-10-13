package com.group4.chatapp.services;

import com.group4.chatapp.dtos.comment.CommentRequestDto;
import com.group4.chatapp.dtos.comment.CommentResponseDto;
import com.group4.chatapp.dtos.comment.CommentDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.*;
import com.group4.chatapp.models.Enum.NotificationType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.repositories.CommentRepository;
import com.group4.chatapp.repositories.ContentRepository;
import com.group4.chatapp.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class CommentService {
    private CommentRepository commentRepository;
    private UserService userService;
    private ContentRepository contentRepository;
    private TargetResolverService targetResolverService;
    private NotificationService notificationService;

    public CommentResponseDto createComment(CommentRequestDto dto) {
        User authUser = userService.getUserOrThrows();

        Long targetId = dto.getTargetId();
        TargetType targetType = dto.getTargetType();
        String content = dto.getContent();

        Long totalCommentByUser = commentRepository.countRootCommentByUserId(authUser.getId(), dto.getTargetId(), dto.getTargetType());

        if (totalCommentByUser < 3) {
            Comment comment = Comment.builder()
                    .user(authUser)
                    .content(content)
                    .targetId(targetId)
                    .targetType(targetType)
                    .build();
            comment = commentRepository.save(comment);

            Notification notification = Notification.builder()
                    .title("Bình luận bài viết mới")
                    .content(authUser.getUsername() + " đã bình luận vào 1 bài viết của bạn")
                    .type(NotificationType.COMMENT)
                    .targetId(dto.getTargetId())
                    .targetType(dto.getTargetType())
                    .build();
            notificationService.notifyAndCreateToUser(
                    authUser,
                    targetResolverService.getAuthor(targetId, targetType), notification
            );

            contentRepository.increaseComments(dto.getTargetId());

            return new CommentResponseDto(comment);
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

    public List<Map<String, Object>> getRootCommentsOfPost(Long targetId, TargetType targetType, int page) {
        List<Object[]> results = commentRepository.findRootCommentsWithChildCount(targetId, targetType, PageRequest.of(page-1, 20));
        List<Map<String, Object>> responses = new ArrayList<>();
        for (Object[] result : results) {
            Comment comment = (Comment) result[0];
            Long totalChildComments = (Long) result[1];
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            responses.add(Map.of(
                    "commentData", commentResponseDto,
                    "totalChildComments", totalChildComments
            ));
        }
        return responses;
    }

    public List<Map<String, Object>> getChildCommentsById(Long commentId, int page) {
        List<Object[]> results = commentRepository.findChildCommentsById(commentId, PageRequest.of(page-1, 5));
        List<Map<String, Object>> responses = new ArrayList<>();
        for (Object[] result : results) {
            Comment comment = (Comment) result[0];
            Long totalChildComments = (Long) result[1];
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            responses.add(Map.of(
                    "commentData", commentResponseDto,
                    "totalChildComments", totalChildComments
            ));
        }
        return responses;
    }

    public void updateComment(CommentDto dto) {
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

    public void replyComment(CommentDto dto) {
        Comment parentComment = getComment(dto.getCommentId());
        Comment newComment = Comment.builder()
                .content(dto.getContent())
                .user(parentComment.getUser())
                .targetId(parentComment.getTargetId())
                .targetType(parentComment.getTargetType())
                .parentComment(parentComment)
                .build();
        contentRepository.increaseComments(parentComment.getTargetId());
        commentRepository.save(newComment);
    }
}
