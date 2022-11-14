package com.base.community.model.repository;


import com.base.community.model.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {



    Page<BoardEntity> findByIdInOrderByIdDesc(List<Long> boardIdList, Pageable pageable);

    Page<BoardEntity> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    Page<BoardEntity> findByCategoryOrderByIdDesc(String category, Pageable pageable);

    Page<BoardEntity> findAllByOrderByIdDesc(Pageable pageable);



    Page<BoardEntity> findByTitleContainingOrderByIdDesc(String keyword, Pageable pageable);

    Page<BoardEntity> findByCategoryAndTitleContainingOrderByIdDesc(String category, String keyword, Pageable pageable);



}
