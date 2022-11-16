package com.base.community.model.repository;

import com.base.community.model.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashSet;
import java.util.List;

public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {

    HashSet<ProjectSkill> findAllByProjectId(Long id);

    void deleteByProjectIdAndNameIn(Long id, List<String> skill);

}
