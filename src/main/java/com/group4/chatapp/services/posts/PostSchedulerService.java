package com.group4.chatapp.services.posts;

import com.group4.chatapp.job.PublishPostJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostSchedulerService {
    private final Scheduler scheduler;

    public void schedulePost(Long postId, LocalDateTime publishTime) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(PublishPostJob.class)
                .withIdentity("publishPostJob-" + postId, "post-jobs")
                .usingJobData("postId", postId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("publishPostTrigger-" + postId, "post-triggers")
                .startAt(Timestamp.valueOf(publishTime))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
