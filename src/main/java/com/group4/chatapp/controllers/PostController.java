package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.ReactionResponseDto;
import com.group4.chatapp.dtos.attachment.PostAttachmentResponseDto;
import com.group4.chatapp.dtos.post.PostRequestDto;
import com.group4.chatapp.dtos.post.PostResponseDto;
import com.group4.chatapp.dtos.post.SharePostDto;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.repositories.PostRepository;
import com.group4.chatapp.services.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/get/{username}")
    public List<PostResponseDto> getPosts(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = "1") int page
    ){
        return postService.getPosts(username, page);
    }

    @PostMapping(value = "/create/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponseDto createPost(@ModelAttribute PostRequestDto dto) {
        return postService.createPost(dto);
    }

    @PutMapping(value = "/update/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePost(
            @RequestParam("postId") Long postId,
            @ModelAttribute PostRequestDto dto
    ) {
        postService.updatePost(postId, dto);
        return ResponseEntity.ok("Update post successfully!");
    }

    @DeleteMapping("/delete/")
    public ResponseEntity<String> deletePost(@RequestParam("postId") Long postId) {
        postService.deletePost(postId);

        return ResponseEntity.ok("Delete post successfully!");
    }

    @GetMapping("/newsfeed/")
    public List<PostResponseDto> getNewsFeed(@RequestParam(value = "page", defaultValue = "1") int page) {
        return postService.getNewsFeed(page);
    }

    @PostMapping("/share/")
    public PostResponseDto share(@Valid @RequestBody SharePostDto dto) {
        return postService.share(dto);
    }

    @GetMapping("/attachments/")
    public List<PostAttachmentResponseDto> getPostAttachments(@NotNull @RequestParam("postId") Long postId) {
        return postService.getPostAttachments(postId);
    }

    @GetMapping("/attachment/get/")
    public PostAttachmentResponseDto getAttachment(@RequestParam("id") Long id) {
        return postService.getPostAttachment(id);
    }

    @PostMapping("/view/increase/")
    public void increaseView(@NotNull @RequestParam("postId") Long postId) {
        postService.increaseView(postId);
    }

    @GetMapping("/reactions/")
    public List<ReactionResponseDto> getReactions(
            @RequestParam("targetId") Long targetId,
            @RequestParam("targetType") TargetType targetType,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        return postService.getReactions(targetId, targetType, page-1);
    }
}
