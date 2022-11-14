package com.base.community.service;

import com.base.community.dto.BoardCommentDto;
import com.base.community.dto.BoardDetailDto;
import com.base.community.dto.BoardDto;
import com.base.community.model.entity.*;
import com.base.community.model.repository.BoardCommentRepository;
import com.base.community.model.repository.BoardRepository;
import com.base.community.model.repository.HeartRepository;
import com.base.community.model.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardCommentRepository boardCommentRepository;

    @Mock
    private HeartRepository heartRepository;

    @Test
    @DisplayName("게시글 작성")
    void write_board(){

        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(boardRepository.save(any())).willReturn(boardEntity);

        //when
        Long saveBoard = boardService.writeBoard(BoardDto.builder()
                        .category("IT")
                        .title("제목")
                        .content("내용입니다.")
                        .build(), 1L);

        //then
        assertEquals(1L, saveBoard);

    }

    @Test
    @DisplayName("게시글 수정")
    void modify_board(){
        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .member(member)
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();

        given(boardRepository.findById(anyLong())).willReturn(Optional.of(boardEntity));

        //when
        Long updateBoard = boardService.modifyBoard(BoardDto.builder()
                .category("IT")
                .title("제목")
                .content("수정입니다.")
                .build(),1L, 1L);

        //then
        assertEquals(1L, updateBoard);
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete_board() {
        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .member(member)
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();

        given(boardRepository.findById(anyLong())).willReturn(Optional.of(boardEntity));

        //when
        String result = boardService.deleteBoard(1L, 1L);

        //then
        assertEquals("삭제가 완료되었습니다.", result);
    }

    @Test
    @DisplayName("전체 게시글 보기 & 카테고리별 게시글 보기")
    void boardList() {
        // given

        List<BoardEntity> boardEntities = new ArrayList<>();
        boardEntities.add(BoardEntity.builder()
                        .id(1L)
                        .category("IT")
                        .title("제목입니다.")
                        .content("내용입니다.")
                        .build());

        Page<BoardEntity> boardEntityPage = new PageImpl<>(boardEntities);
        Pageable pageable = PageRequest.of(0, 10);

        given(boardRepository.findByCategoryOrderByIdDesc("IT", (PageRequest) pageable)).willReturn(boardEntityPage);

        //when
        Page<BoardEntity> board = boardService.boardList("IT","",0);

        //then
        assertEquals("IT", board.getContent().get(0).getCategory());
        assertEquals("제목입니다.", board.getContent().get(0).getTitle());
        assertEquals("내용입니다.", board.getContent().get(0).getContent());
        assertEquals(1, board.getTotalPages());

    }

    @Test
    @DisplayName("내가 쓴 글 목록 보기")
    void myBoardList() {
        // given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        List<BoardEntity> boardEntities = new ArrayList<>();
        boardEntities.add(BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목입니다.")
                .content("내용입니다.")
                .member(member)
                .build());

        Page<BoardEntity> boardEntityPage = new PageImpl<>(boardEntities);
        Pageable pageable = PageRequest.of(0, 10);

        given(boardRepository.findByMemberIdOrderByIdDesc(member.getId(), (PageRequest) pageable)).willReturn(boardEntityPage);

        //when
        Page<BoardEntity> board = boardService.myBoardList(1L,0);

        //then
        assertEquals("IT", board.getContent().get(0).getCategory());
        assertEquals("제목입니다.", board.getContent().get(0).getTitle());
        assertEquals("내용입니다.", board.getContent().get(0).getContent());
        assertEquals(1, board.getTotalPages());

    }

    @Test
    void myHeartList() {
        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        List<BoardEntity> boardEntities = new ArrayList<>();
        boardEntities.add(BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목입니다.")
                .content("내용입니다.")
                .member(member)
                .build());

        Page<BoardEntity> boardEntityPage = new PageImpl<>(boardEntities);
        Pageable pageable = PageRequest.of(0, 10);

        List <HeartEntity> hearts = heartRepository.findByMemberId(member.getId());

        List<Long> boardIdList = new ArrayList<>();

        for(HeartEntity heart : hearts){
            boardIdList.add(heart.getBoardId());
        }

        given(boardRepository.findByIdInOrderByIdDesc(boardIdList, (PageRequest) pageable)).willReturn(boardEntityPage);


        //when
        Page<BoardEntity> heart = boardService.myHeartList(1L, (PageRequest) pageable);

        //then
        assertEquals(1, heart.getTotalPages());

    }

    @Test
    @DisplayName("좋아요")
    void heart() {
        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();

        HeartEntity heartEntity = HeartEntity.builder()
                .memberId(member.getId())
                .boardId(boardEntity.getId())
                .build();

        //when
        Boolean heart = boardService.heart(heartEntity.getMemberId(),
                heartEntity.getBoardId());

        //then
        assertEquals(true,heart);

    }

    @Test
    @DisplayName("게시글 댓글 작성")
    void write_Board_Comment() {
        // given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(boardEntity));
        given(boardCommentRepository.save(any())).willReturn(BoardCommentEntity.builder()
                .id(1L)
                .member(member)
                .boardEntity(boardEntity)
                .content("댓글입니다.")
                .build());

        //when
        Long boardCommentEntity = boardService.writeBoardComment(
                BoardCommentDto.builder()
                        .content("댓글입니다.")
                        .build(),1L,1L);

        assertEquals(1L,boardCommentEntity);
    }

    @Test
    @DisplayName("게시글 댓글 수정")
    void modify_Board_Comment() {
        // given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity boardEntity = BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목")
                .content("내용입니다.")
                .build();

        BoardCommentEntity boardComment = BoardCommentEntity.builder()
                .id(1L)
                .content("댓글입니다.")
                .member(member)
                .boardEntity(boardEntity)
                .build();

       given(boardCommentRepository.findById(anyLong())).willReturn(Optional.of(boardComment));

        //when
        Long boardCommentEntity = boardService.modifyComment(
                BoardCommentDto.builder()

                        .content("수정입니다..")
                        .build(),member.getId(),boardComment.getId());

        assertEquals(1L,boardCommentEntity);
    }

    @Test
    @DisplayName("게시글 댓글 삭제")
    void delete_Board_Comment() {
        //given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardCommentEntity boardComment = BoardCommentEntity.builder()
                .member(member)
                .id(1L)
                .content("댓글입니다.")
                .build();

        given(boardCommentRepository.findById(anyLong())).willReturn(Optional.of(boardComment));

        //when
        String result = boardService.deleteComment(1L,1L);
        //then

        assertEquals("댓글이 삭제되었습니다.", result);
    }

    @Test
    @DisplayName("게시글 상세 보기")
    void board_Detail() {
        // given
        List<MemberSkills> memberSkills = new ArrayList<>();
        memberSkills.add(MemberSkills.builder().name("java").build());
        memberSkills.add(MemberSkills.builder().name("spring").build());
        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(memberSkills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        BoardEntity board = BoardEntity.builder()
                .id(1L)
                .category("IT")
                .title("제목입니다.")
                .content("테스트 입니다.")
                .member(member)
                .build();

       given(boardRepository.findById(1L)).willReturn(Optional.of(board));

            //when
        BoardDetailDto detail =boardService.boardDetail(1L);

        //then
        assertEquals(1L, detail.getBoardId());
        assertEquals("IT", detail.getCategory());
        assertEquals("제목입니다.", detail.getTitle());
        assertEquals("테스트 입니다.", detail.getContent());
        assertEquals("멍멍이", detail.getNickname());

    }
}