package com.base.community.model.repository;


import com.base.community.model.entity.MemberSkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSkillsRepository extends JpaRepository<MemberSkills, Long> {

    Optional<MemberSkills> findByMemberIdAndId(Long memberId, Long skillId);
}
