package com.base.community.service;

import com.base.community.dto.ProjectDto;
import com.base.community.dto.ProjectSkillDto;
import com.base.community.model.entity.*;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectMemberRepository;
import com.base.community.model.repository.ProjectRepository;
import com.base.community.model.repository.ProjectSkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectSkillRepository projectSkillRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Test
    @DisplayName("프로젝트 전체 보기")
    void get_project_test() {
        // given
        List<Project> projects = new ArrayList<>();
        projects.add(Project.builder()
                .id(1L)
                .title("프로젝트 모집")
                .content("프로젝트 모집합니다.")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .build());
        Page<Project> projectPage = new PageImpl<>(projects);
        Pageable pageable = PageRequest.of(0, 10);
        given(projectRepository.findAll(pageable)).willReturn(projectPage);

        // when
        Page<Project> project = projectService.getProject(pageable, null);

        // then
        assertEquals("프로젝트 모집", project.getContent().get(0).getTitle());
        assertEquals("프로젝트 모집합니다.", project.getContent().get(0).getContent());
        assertEquals(5, project.getContent().get(0).getMaxTotal());
        assertEquals(0, project.getContent().get(0).getNowTotal());
        assertEquals("3", project.getContent().get(0).getDevelopPeriod());
        assertEquals(LocalDate.now(), project.getContent().get(0).getStartDate());
    }

    @Test
    @DisplayName("프로젝트 상세 보기")
    void get_project_detail_test() {
        // given
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트 모집")
                .content("프로젝트 모집합니다.")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .build();

        given(projectRepository.findById(1L)).willReturn(Optional.of(project));

        // when
        Project projectDetail = projectService.getProjectDetail(1L);

        // then
        assertEquals(1L, project.getId());
        assertEquals("프로젝트 모집", project.getTitle());
        assertEquals("프로젝트 모집합니다.", project.getContent());
        assertEquals(5, project.getMaxTotal());
        assertEquals(0, project.getNowTotal());
        assertFalse(project.isComplete());
        assertEquals("3", project.getDevelopPeriod());
        assertEquals(LocalDate.now(), project.getStartDate());
    }

    @Test
    @DisplayName("프로젝트 생성")
    void create_project() {
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
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectRepository.findByLeader(any())).willReturn(Optional.ofNullable(null));
        given(projectRepository.save(any())).willReturn(project);

        // when
        List<ProjectSkillDto> skillDtos = new ArrayList<>();
        skillDtos.add(ProjectSkillDto.builder()
                .name("react")
                .build());
        Project saveProject = projectService.createProject(1L, ProjectDto.builder()
                .title("스프링 프로젝트 하실분")
                .content("스프링 프로젝트 멤버 구합니다.")
                .maxTotal(3)
                .startDate(LocalDate.now())
                .developPeriod("2")
                .projectSkills(skillDtos)
                .build());

        // then
        assertEquals(1L, saveProject.getId());
        assertEquals("프로젝트", saveProject.getTitle());
        assertEquals("내용", saveProject.getContent());
        assertEquals(5, saveProject.getMaxTotal());
        assertEquals(0, saveProject.getNowTotal());
        assertEquals("3", saveProject.getDevelopPeriod());
    }

    @Test
    @DisplayName("프로젝트 수정")
    void update_project_test() {
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
                .maxTotal(5)
                .nowTotal(0)
                .leader(member)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();
        given(projectRepository.findById(anyLong())).willReturn(Optional.of(project));

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);

        // when
        List<ProjectSkillDto> skillDtos = new ArrayList<>();
        skillDtos.add(ProjectSkillDto.builder()
                .name("react")
                .build());
        Project saveProject = projectService.updateProject(1L, ProjectDto.builder()
                .id(1L)
                .title("스프링 프로젝트 하실분")
                .content("스프링 프로젝트 멤버 구합니다.")
                .maxTotal(3)
                .startDate(LocalDate.now())
                .developPeriod("2")
                .projectSkills(skillDtos)
                .build());

        // then
        //verify(projectRepository, times(1)).save(captor.capture());
        assertEquals(1L, saveProject.getId());
        assertEquals("스프링 프로젝트 하실분", saveProject.getTitle());
        assertEquals("스프링 프로젝트 멤버 구합니다.", saveProject.getContent());
        assertEquals(3, saveProject.getMaxTotal());
        assertEquals(0, saveProject.getNowTotal());
        assertEquals("2", saveProject.getDevelopPeriod());
    }

    @Test
    @DisplayName("프로젝트 삭제")
    void delete_project() {
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
                .maxTotal(5)
                .nowTotal(0)
                .leader(member)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectRepository.findByLeader(any())).willReturn(Optional.of(project));

        // when
        String result = projectService.deleteProject(1L, 1L);

        // then
        assertEquals("삭제가 완료되었습니다.", result);
    }

    @Test
    @DisplayName("프로젝트 스킬 삭제")
    void delete_project_skill_test() {
        // given
        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();
        given(projectRepository.findById(anyLong())).willReturn(Optional.of(project));

        // when
        String result = projectService.deleteProjectSkill(1L);

        // then
        assertEquals("삭제가 완료되었습니다.", result);
    }

    @DisplayName("프로젝트 신청")
    @Test
    void register_project_test() {
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

        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectRepository.findById(anyLong())).willReturn(Optional.of(project));
        given(projectMemberRepository.save(any())).willReturn(ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(member)
                .accept(false)
                .build());

        //when
        ProjectMember projectMember = projectService.registerProjectMember(1L, 1L);

        //then
        assertEquals("프로젝트", projectMember.getProject().getTitle());
        assertEquals(5, projectMember.getProject().getMaxTotal());
        assertEquals(1, projectMember.getProject().getNowTotal());
        assertEquals("test@test.com", projectMember.getMember().getEmail());
    }

    @DisplayName("프로젝트 수락")
    @Test
    void accept_project_test() {
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

        List<ProjectSkill> projectSkills = new ArrayList<>();
        projectSkills.add(ProjectSkill.builder().name("spring").build());
        projectSkills.add(ProjectSkill.builder().name("react").build());
        Project project = Project.builder()
                .id(1L)
                .title("프로젝트")
                .content("내용")
                .maxTotal(5)
                .nowTotal(0)
                .isComplete(false)
                .developPeriod("3")
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(member)
                .accept(true)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectMemberRepository.findByMember(any())).willReturn(Optional.of(projectMember));
        given(projectRepository.findById(anyLong())).willReturn(Optional.of(project));

        //when
        ProjectMember saveProjectMember = projectService.acceptProjectMember(1L);

        //then
        assertTrue(saveProjectMember.isAccept());
        assertEquals(1, saveProjectMember.getProject().getNowTotal());
    }

    @DisplayName("내 프로젝트")
    @Test
    void get_my_project_test() {
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
                .startDate(LocalDate.now())
                .projectSkills(projectSkills)
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(member)
                .accept(true)
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(projectMemberRepository.findByMember(any())).willReturn(Optional.of(projectMember));
        given(projectRepository.findById(anyLong())).willReturn(Optional.of(project));

        // when
        Project myProjectList = projectService.myProjectList(1L);

        // then
        assertEquals(1L, myProjectList.getId());
    }
}