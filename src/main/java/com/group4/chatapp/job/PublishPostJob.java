package com.group4.chatapp.job;

import com.group4.chatapp.models.Post;
import com.group4.chatapp.repositories.PostRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;

public class PublishPostJob implements Job {
    private PostRepository postRepository;

    @Override
    public void execute(JobExecutionContext context) {
        Long postId = context.getJobDetail().getJobDataMap().getLong("postId");

        postRepository.findById(postId).ifPresent(post -> {
            if (post.getStatus() == Post.PostStatus.SCHEDULED &&
                    post.getScheduledAt().isBefore(LocalDateTime.now())) {

                post.setStatus(Post.PostStatus.PUBLISHED);
                post.setPublishedAt(LocalDateTime.now());
                postRepository.save(post);

                System.out.println("✅ Published post: " + postId);
            }
        });
    }
}
