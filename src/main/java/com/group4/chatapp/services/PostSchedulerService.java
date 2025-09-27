package com.group4.chatapp.services;

import com.group4.chatapp.jobs.PublishPostJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PostSchedulerService {

    private final Scheduler scheduler;

    public void schedulePost(Long postId, LocalDateTime scheduledAt) throws SchedulerException {
        if (scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian phải lớn hơn hiện tại");
        }

        JobDetail jobDetail = JobBuilder.newJob(PublishPostJob.class)
                .withIdentity("postJob-" + postId, "post-jobs")
                .usingJobData("postId", postId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("postTrigger-" + postId, "post-triggers")
                .startAt(Date.from(scheduledAt.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        System.out.println("✅ Scheduled postId " + postId + " at " + scheduledAt);
    }
}


