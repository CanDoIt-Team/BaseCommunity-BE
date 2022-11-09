package com.base.community.model.repository;

import com.base.community.model.entity.Member;
import com.base.community.model.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByMember(Member member);
    boolean existsByMember(Member member);
}
