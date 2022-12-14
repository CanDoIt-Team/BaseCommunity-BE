package com.base.community.service;


import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.*;
import com.base.community.model.repository.BoardCommentRepository;
import com.base.community.model.repository.BoardRepository;
import com.base.community.model.repository.HeartRepository;
import com.base.community.model.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.base.community.exception.ErrorCode.*;

@Slf4j
@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final BoardCommentRepository boardCommentRepository;

    //게시글 전체보기
    public Page<BoardEntity> boardList(String category, String keyword,final Pageable pageable) {

        Page<BoardEntity> boards;//전체 조회
        if (category == null && keyword == null) {
            log.info("실행됨 keyword && category != null");
            boards = boardRepository.findAllByOrderByIdDesc(pageable);
        } else if (category != null && keyword == null) {
            log.info("실행됨 category != null");
            boards = boardRepository.findByCategoryOrderByIdDesc(category, pageable);
        } else if (keyword != null && category == null) {
            log.info("실행됨 keyword != null");
            boards = boardRepository.findByTitleContainingOrderByIdDesc(keyword, pageable);
        } else {
            log.info("실행됨 keyword && category != null");
            boards = boardRepository.findByCategoryAndTitleContainingOrderByIdDesc(category, keyword, pageable);
        }
        return boards;
    }

    // 내가 작성한 글 목록
    public Page<BoardEntity> myBoardList(Long memberId, final Pageable pageable) {

        Page<BoardEntity> boards;
        boards = boardRepository.findByMemberIdOrderByIdDesc(memberId, pageable);

        return boards;
    }

    // 내가 좋아요한 글 목록
    public Page<BoardEntity> myHeartList(Long memberId, final Pageable pageable) {
        List<HeartEntity> hearts = heartRepository.findByMemberId(memberId);
        List<Long> boardIdList = new ArrayList<>();
        for (HeartEntity heart : hearts) {
            boardIdList.add(heart.getBoardId());
        }

        return boardRepository.findByIdInOrderByIdDesc(boardIdList, pageable);
    }


    @Transactional
    // 게시판 글 작성
    public Long writeBoard(BoardDto boardDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        BoardEntity board = boardRepository.save(BoardEntity.from(boardDto, member));

        return board.getId();
    }

    // 게시판 글 수정
    @Transactional
    public Long modifyBoard(BoardDto boardDto, Long memberId, Long boardId) {
        var board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        if (!Objects.equals(board.getMember().getId(), memberId)) { // 작성자만 수정 가능
            throw new CustomException(NOT_AUTHORITY_BOARD_MODIFY);
        }
        board.setCategory(boardDto.getCategory());
        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());

        return board.getId();
    }

    // 게시판 글 삭제
    @Transactional
    public String deleteBoard(Long memberId, Long boardId) {
        var board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        if (!Objects.equals(board.getMember().getId(), memberId)) { // 작성자만 삭제 가능
            throw new CustomException(NOT_AUTHORITY_BOARD_DELETE);
        }
        boardRepository.deleteById(boardId);

        return "삭제가 완료되었습니다.";
    }

    //게시글 좋아요
    public boolean heart(Long memberId, Long boardId) {

        boolean check = heartRepository.existsById(new HeartId(memberId, boardId));

        if (check) {
            heartRepository.deleteById(new HeartId(memberId, boardId));

            return false;
        }
        heartRepository.save(new HeartEntity(memberId, boardId));

        return true;
    }

    //게시글 댓글작성
    @Transactional
    public Long writeBoardComment(BoardCommentDto dto, Long memberId, Long boardId) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        var board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        BoardCommentEntity boardCommentEntity =
                boardCommentRepository.save(BoardCommentEntity.from(dto, member, board));

        return boardCommentEntity.getId();
    }

    //게시글 댓글수정
    @Transactional
    public Long modifyComment(BoardCommentDto dto, Long memberId, Long commentId) {

        var boardComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD_COMMENT));

        if (!Objects.equals(boardComment.getMember().getId(), memberId)) { // 작성자만 수정 가능
            throw new CustomException(NOT_AUTHORITY_COMMENT_MODIFY);
        } else {
            boardComment.setContent(dto.getContent());

            return boardComment.getId();
        }

    }

    //게시글 댓글삭제
    @Transactional
    public String deleteComment(Long memberId, Long commentId) {
        var boardComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD_COMMENT));

        if (!Objects.equals(boardComment.getMember().getId(), memberId)) {
            throw new CustomException(NOT_AUTHORITY_COMMENT_DELETE);

        }
        boardCommentRepository.deleteById(commentId);

        return "댓글이 삭제되었습니다.";
    }

    // 게시글 상세보기

    public BoardDetailDto boardDetail(Long boardId) {

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOARD));

        List<BoardCommentDetailDto> commentList = new ArrayList<>();
        for (BoardCommentEntity boardCommentEntity : board.getComments()) {
            BoardCommentDetailDto comment = BoardCommentDetailDto.builder()
                    .boardId(boardCommentEntity.getBoardEntity().getId())
                    .commentId(boardCommentEntity.getId())
                    .memberId(boardCommentEntity.getMember().getId())
                    .nickname(boardCommentEntity.getMember().getNickname())
                    .urlFilename(boardCommentEntity.getMember().getUrlFilename())
                    .content(boardCommentEntity.getContent())
                    .createAt(boardCommentEntity.getCreatedAt())
                    .updateAt(boardCommentEntity.getModifiedAt())
                    .build();

            commentList.add(comment);
        }

        return BoardDetailDto.builder()
                .boardId(board.getId())
                .category(board.getCategory())
                .title(board.getTitle())
                .nickname(board.getMember().getNickname())
                .urlFilename(board.getMember().getUrlFilename())
                .content(board.getContent())
                .createAt(board.getCreatedAt())
                .updateAt(board.getModifiedAt())
                .comments(commentList)
                .build();
    }
}
