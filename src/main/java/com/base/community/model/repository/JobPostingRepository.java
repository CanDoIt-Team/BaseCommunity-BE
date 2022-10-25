package com.base.community.model.repository;

import com.base.community.model.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, String> {
}
