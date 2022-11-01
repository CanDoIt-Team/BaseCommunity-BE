package com.base.community.model.repository;


import com.base.community.model.entity.MemberSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

public interface MemberSkillsRepository extends JpaRepository<MemberSkills, Long> {


    @Transactional
    void deleteByMemberIdAndNameIn(Long id, List<String> skill);

    HashSet<MemberSkills> findAllByMemberId(Long id);
}
