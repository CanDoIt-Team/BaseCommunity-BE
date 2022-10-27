package com.base.community.config;

import com.base.community.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobPostingService jobPostingService;


    @Bean
    public Job Job() {
        return jobBuilderFactory.get("Job")
                .start(this.Step2())
                .next(this.Step1())
                .build();
    }

    @Bean
    @JobScope
    public Step Step1(){
        return stepBuilderFactory.get("Step1")
                .tasklet((contribution, chunkContext) -> {
                    jobPostingService.insertJopPosting();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step Step2(){
        return  stepBuilderFactory.get("Step2")
                .tasklet((contribution, chunkContext) -> {
                    jobPostingService.deleteJobPosting();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
