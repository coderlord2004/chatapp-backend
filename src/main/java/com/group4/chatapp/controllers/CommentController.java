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
    public List<CommentResponseDto> getComments(@RequestParam("targetId") Long targetId, @RequestParam("targetType") TargetType targetType) {
        return commentService.getComments(targetId, targetType);
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
