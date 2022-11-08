package com.base.community.service;

import com.base.community.model.entity.JobPosting;
import com.base.community.model.repository.JobPostingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private JobPostingService jobPostingService;


    @DisplayName("채용공고 전체보기")
    @Test
    void get_posting_test() {
        List<JobPosting> jobPostings = new ArrayList<>();

        jobPostings.add(JobPosting.builder()
                .wantedAuthNo("AAAA123456789")
                .basicAddr("@@시 ##구 $$로 11-1")
                .busino("123456789")
                .career("관계없음")
                .closeDt("23-01-31, 채용시 마감")
                .company("제로베이스 협업 5조 회사")
                .detailAddr("각자집")
                .empTpCd("10")
                .holidayTpNm("주 7일제")
                .infoSvc("VALIDATION")
                .jobsCd("133100")
                .maxEdubg(null)
                .maxSal(null)
                .minEdubg("학력무관")
                .minSal("3000")
                .prefCd(null)
                .regDt("22-11-06")
                .region("@@시 ##구")
                .sal("5000원")
                .salTpNm("연봉")
                .smodifyDtm("202211061950")
                .strtnmCd("116803122010")
                .title("[급구]유저정보 테스트 코드 짜주실분 구합니다.")
                .wantedInfoUrl("http://localhost:8080")
                .wantedMobileInfoUrl("http://localhost:8080")
                .zipCd("00000")
                .build());

        Page<JobPosting> jobPostingPage = new PageImpl<>(jobPostings);
        Pageable pageable = PageRequest.of(0, 10);

        given(jobPostingRepository.findAll(pageable)).willReturn(jobPostingPage);

        //when
        Page<JobPosting> jobPosting = jobPostingService.getPosting(pageable);


        //then
        assertEquals("[급구]유저정보 테스트 코드 짜주실분 구합니다.", jobPosting.getContent().get(0).getTitle());
    }

}