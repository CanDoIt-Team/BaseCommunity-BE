package com.base.community.model.repository;

import com.base.community.model.entity.HeartEntity;
import com.base.community.model.entity.HeartId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartRepository extends JpaRepository<HeartEntity, HeartId> {

    List<HeartEntity> findByMemberId(Long memberId);
}
