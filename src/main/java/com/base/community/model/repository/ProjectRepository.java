package com.base.community.model.repository;

import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByLeader(Member member);
}
