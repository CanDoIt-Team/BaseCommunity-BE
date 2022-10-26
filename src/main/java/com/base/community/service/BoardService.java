package com.base.community.service;


import com.base.community.dto.BoardCommentDto;
import com.base.community.dto.BoardDto;
import com.base.community.dto.BoardListResDto;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.*;
import com.base.community.model.repository.BoardCommentRepository;
import com.base.community.model.repository.BoardRepository;
import com.base.community.model.repository.HeartRepository;
import com.base.community.model.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.base.community.exception.ErrorCode.*;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final BoardCommentRepository boardCommentRepository;

    //게시글 전체보기
    @Transactional
    public Map<String, Object> boardList(String category, int page) {

        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<BoardEntity> boards;

        if (category == null) { //전체 조회
            boards = boardRepository.findAll(pageRequest);
        } else { //카테고리 조회
            boards = boardRepository.findByCategory(category, pageRequest);
        }

        return makeList(boards);
    }

    private Map<String, Object> makeList(Page<BoardEntity> boards) {

        Map<String, Object> resultMap = new HashMap<>();

        if(boards.isEmpty()) {
            resultMap.put("message", ErrorCode.NOT_FOUND_BOARD);
            return resultMap;
        }

        List<BoardListResDto> list = new ArrayList<>();
        for (BoardEntity boardEntity : boards) {
            BoardListResDto boardListResDto = BoardListResDto.builder()
                    .category(boardEntity.getCategory())
                    .title(boardEntity.getTitle())
                    .nickname(boardEntity.getMember().getNickname())
                    .createdAt(boardEntity.getCreatedAt())
                    .build();

            list.add(boardListResDto);
        }

        resultMap.put("board", list);
        resultMap.put("totalPage", boards.getTotalPages());
        return resultMap;
    }

    // 게시판 글 작성
    @Transactional
    public Long writeBoard(BoardDto boardDto, Long memberId){
        BoardEntity boardEntity =
                boardRepository.save(BoardEntity.from(boardDto , memberRepository.findById(memberId).get()));
        return boardEntity.getId();
    }

    // 게시판 글 수정
    @Transactional
    public Long modifyBoard(BoardDto boardDto, Long memberId,Long boardId ){

       if(boardRepository.findById(boardId).get().getMember().getId() == memberId){

           BoardEntity boardEntity = boardRepository.findById(boardId).get();

           boardEntity.setCategory(boardDto.getCategory());
           boardEntity.setTitle(boardDto.getTitle());
           boardEntity.setContent(boardDto.getContent());
           return boardEntity.getId();

       } else {
           throw new CustomException(NOT_AUTHORITY_BOARD_MODIFY);
       }
    }

    // 게시판 글 삭제
    @Transactional
    public void deleteBoard(Long memberId, Long boardId){

        if(boardRepository.findById(boardId).get().getMember().getId() == memberId){
            boardRepository.deleteById(boardId);
        } else {
            throw new CustomException(NOT_AUTHORITY_BOARD_DELETE);
        }
    }

    //게시글 좋아요
    @Transactional
    public boolean heart(Long memberId, Long boardId){

        Optional<Member> member = memberRepository.findById(memberId);
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardId);
        boolean check = heartRepository.existsById(new HeartId(memberId, boardId));

        if(check){
            heartRepository.deleteById(new HeartId(memberId, boardId));
            return false;
        }
        heartRepository.save(new HeartEntity(memberId, boardId));
        return true;
    }

    //게시글 댓글작성
    @Transactional
    public Long writeBoardComment(BoardCommentDto dto, Long memberId, Long boardId) {
        BoardCommentEntity boardCommentEntity =
               boardCommentRepository.save(BoardCommentEntity.from(dto, memberRepository.findById(memberId).get(),boardRepository.findById(boardId).get()));

        return boardCommentEntity.getId();
    }


    //게시글 댓글수정
    @Transactional
    public Long modifyComment(BoardCommentDto dto, Long memberId,Long commentId) {

            if (boardCommentRepository.findById(commentId).get().getMember().getId() == memberId){
                BoardCommentEntity boardCommentEntity =
                        boardCommentRepository.findById(commentId).get();
                boardCommentEntity.setContent(dto.getContent());
                return boardCommentEntity.getId();
            } else {
                throw new CustomException(NOT_AUTHORITY_COMMENT_MODIFY);
            }

    }


    //게시글 댓글삭제
    @Transactional
    public void deleteComment(Long memberId, Long commentId) {

        if (boardCommentRepository.findById(commentId).get().getMember().getId() == memberId){
            boardCommentRepository.deleteById(commentId);
        } else {
            throw new CustomException(NOT_AUTHORITY_COMMENT_DELETE);
        }



    }
}
