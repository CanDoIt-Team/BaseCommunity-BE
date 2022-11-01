package com.base.community.model.repository;

import com.base.community.model.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, String> {

   @Query("SELECT COUNT(wantedAuthNo) FROM JobPosting ")
    Long getCount();

   @Query("SELECT wantedAuthNo FROM JobPosting")
   List<String> getNo();
}
