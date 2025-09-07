package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.ReactionDto;
import com.group4.chatapp.services.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reaction/")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/post/save/")
    public void saveReaction(@RequestBody ReactionDto reactionDto) {
        reactionService.saveReaction(reactionDto);
    }


}
