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
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("postId", postId);

        JobDetail jobDetail = JobBuilder.newJob(PublishPostJob.class)
                .withIdentity("postJob-" + postId, "post-jobs")
                .usingJobData(dataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("postTrigger-" + postId, "post-triggers")
                .startAt(Date.from(scheduledAt.atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}


