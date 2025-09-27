package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.comment.CommentRequestDto;
import com.group4.chatapp.dtos.comment.CommentResponseDto;
import com.group4.chatapp.dtos.comment.CommentDto;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comment/")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create/")
    public void createComment(@Valid @RequestBody CommentRequestDto dto) {
        commentService.createComment(dto);
    }

    @GetMapping("/get/")
    public List<Map<String, Object>> getComments(
            @RequestParam("targetId") Long targetId,
            @RequestParam("targetType") TargetType targetType,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return commentService.getRootCommentsOfPost(targetId, targetType, page);
    }

    @GetMapping("/child/get/")
    public List<Map<String, Object>> getChildComments(
            @RequestParam("commentId") Long commentId,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return commentService.getChildCommentsById(commentId, page);
    }

    @PatchMapping("/update/")
    public void updateComment(@RequestBody CommentDto dto) {
        commentService.updateComment(dto);
    }

    @DeleteMapping("/delete/")
    public void deleteComment(@RequestParam("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PostMapping("/reply/")
    public void replyComment(@RequestBody CommentDto dto) {
        commentService.replyComment(dto);
    }
}
