package com.base.community.model.repository;

import com.base.community.model.entity.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
}
