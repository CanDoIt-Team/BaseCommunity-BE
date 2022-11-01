package com.base.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BatchScheduler {


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BatchConfig batchConfig;


    @Scheduled(cron = "0 0/30 * * * *")
    public void runJob(){
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time",new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try{
            jobLauncher.run(batchConfig.Job(), jobParameters);

        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobParametersInvalidException | JobRestartException  e) {
            log.error(e.getMessage());
        }

    }
}
