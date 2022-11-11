package com.base.community.model.repository;


import com.base.community.model.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {



    Page<BoardEntity> findByIdInOrderByIdDesc(List<Long> boardIdList, Pageable pageable);

    Page<BoardEntity> findByMemberIdOrderByIdDesc(Long memberId, PageRequest pageRequest);

    Page<BoardEntity> findByCategoryOrderByIdDesc(String category, PageRequest pageRequest);

    Page<BoardEntity> findAllByOrderByIdDesc(PageRequest pageRequest);



    Page<BoardEntity> findByTitleContainingOrderByIdDesc(String keyword, PageRequest pageRequest);

    Page<BoardEntity> findByCategoryAndTitleContainingOrderByIdDesc(String category, String keyword, PageRequest pageRequest);



}
