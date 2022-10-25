package com.base.community.model.repository;

import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {
    Iterable<ProjectSkill> findByProject(Project project);
    void deleteByProject(Project project);
}
