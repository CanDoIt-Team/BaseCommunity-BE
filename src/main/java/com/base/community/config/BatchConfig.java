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
    public Job testJob() {
        return jobBuilderFactory.get("testJob")
                .start(this.testStep())
                .build();
    }

    @Bean
    @JobScope
    public Step testStep(){
        return stepBuilderFactory.get("testStep")
                .tasklet((contribution, chunkContext) -> {
                    jobPostingService.insertJopPosting();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
