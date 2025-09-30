package com.group4.chatapp.services;

import com.group4.chatapp.models.Post;
import com.group4.chatapp.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSchedulerService {
    private final PostRepository postRepository;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void checkAndPublishPosts() throws SchedulerException {
        List<Post> readyPosts = postRepository.findReadyToPublish(LocalDateTime.now());
        for (Post readyPost : readyPosts) {
            readyPost.setPublishedAt(LocalDateTime.now());
            readyPost.setStatus(Post.PostStatus.PUBLISHED);
            postRepository.save(readyPost);
            System.out.println("Published postId: " + readyPost.getId() + " at " + LocalDateTime.now());
        }
    }
}


