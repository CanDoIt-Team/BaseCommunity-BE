package com.base.community.service;

import com.base.community.dto.ProjectCommentDto;
import com.base.community.model.entity.*;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectCommentRepository;
import com.base.community.model.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectCommentServiceTest {

    @InjectMocks
    private ProjectCommentService projectCommentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectCommentRepository projectCommentRepository;

    @DisplayName("프로젝트 댓글 작성")
    @Test
    void add_comment_test() {
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


        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .leader(member)
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .projectSkills(projectSkills)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));
        given(projectRepository.findById(anyLong())).willReturn(Optional.ofNullable(project));
        given(projectCommentRepository.save(any())).willReturn(ProjectComment.builder()
                .id(1L)
                .project(project)
                .member(member)
                .content("주요 일정이 어떻게 되나요?")
                .build());

        // when
        ProjectComment projectComment = projectCommentService.addComment(anyLong(),
                ProjectCommentDto.builder()
                        .content("댓글입니다.")
                        .projectId(1L).build());

        // then
        assertEquals(1L, projectComment.getId());
        assertEquals("주요 일정이 어떻게 되나요?", projectComment.getContent());
        assertEquals(1L, projectComment.getMember().getId());
        assertEquals(1L, projectComment.getProject().getId());
    }

    @Test
    @DisplayName("프로젝트 댓글 수정")
    public void update_comment_test() {
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


        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .leader(member)
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .projectSkills(projectSkills)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));
        given(projectRepository.findById(anyLong())).willReturn(Optional.ofNullable(project));
        given(projectCommentRepository.findById(anyLong())).willReturn(Optional.of(ProjectComment.builder()
                .id(1L)
                .content("주요 일정이 어떻게 되나요?")
                .build()));

        // when
        ProjectComment projectComment = projectCommentService.updateComment(1L,
                ProjectCommentDto.builder()
                        .content("댓글입니다.")
                        .id(1L)
                        .projectId(1L).build());

        // then
        assertEquals(1L, projectComment.getId());
        assertEquals("댓글입니다.", projectComment.getContent());
    }

    @Test
    @DisplayName("프로젝트 댓글 삭제")
    public void delete_comment_test() {
        ProjectComment projectComment = ProjectComment.builder()
                .id(1L)
                .content("댓글")
                .build();

        given(projectCommentRepository.findById(any())).willReturn(Optional.ofNullable(projectComment));

        ArgumentCaptor<ProjectComment> captor = ArgumentCaptor.forClass(ProjectComment.class);

        // when
        ProjectComment comment = projectCommentService.deleteComment(anyLong());

        assertEquals(1L, comment.getId());
        assertEquals("댓글", comment.getContent());
    }
}