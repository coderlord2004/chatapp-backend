package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.comment.CommentCreationDto;
import com.group4.chatapp.dtos.comment.CommentResponseDto;
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
    public void createComment(@Valid @RequestBody CommentCreationDto dto) {
        commentService.createComment(dto);
    }

    @GetMapping("/get/")
    public List<CommentResponseDto> getComments(@RequestParam("targetId") Long targetId, @RequestParam("targetType") TargetType targetType) {
        return commentService.getComments(targetId, targetType);
    }
}
