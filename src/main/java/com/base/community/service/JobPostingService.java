package com.base.community.service;

import com.base.community.model.entity.JobPosting;
import com.base.community.model.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingAPI jobPostingAPI;
    private final JobPostingRepository jobPostingRepository;

    public void insertJopPosting() {

        jobPostingAPI.jobPosting();
    }



    public void deleteJobPosting() throws IOException, ParserConfigurationException, SAXException {
        jobPostingAPI.deleteJobPosting();
    }

    public Page<JobPosting> getPosting(Pageable pageable) {
        return jobPostingRepository.findAll(pageable);
    }



}
