package com.group4.chatapp.jobs;

import com.group4.chatapp.models.Post;
import com.group4.chatapp.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PublishPostJob implements Job {
    private final PostRepository postRepository;

    @Override
    public void execute(JobExecutionContext context) {
        Long postId = context.getJobDetail().getJobDataMap().getLong("postId");

        postRepository.findById(postId).ifPresent(post -> {
            post.setStatus(Post.PostStatus.PUBLISHED);
            post.setPublishedAt(LocalDateTime.now());
            postRepository.save(post);
            System.out.println(">>> Post #" + postId + " đã được publish lúc " + LocalDateTime.now());
        });
    }
}
