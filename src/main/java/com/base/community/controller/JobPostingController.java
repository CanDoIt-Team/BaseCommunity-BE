package com.base.community.controller;


import com.base.community.model.entity.JobPosting;
import com.base.community.service.JobPostingService;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/employments")
public class JobPostingController {


    private final JobPostingService jobPostingService;


    @ApiOperation(value = "채용정보 리스트")
    @GetMapping()
    public ResponseEntity<?> showPosting(final Pageable pageable) {
        Page<JobPosting> postings = jobPostingService.getPosting(pageable);

        return ResponseEntity.ok(postings);
    }
}
