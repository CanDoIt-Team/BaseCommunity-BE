package com.base.community.model.repository;

import com.base.community.model.entity.BoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository <BoardCommentEntity, Long> {
}
