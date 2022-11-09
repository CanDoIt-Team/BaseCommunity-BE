package com.base.community.service;

import com.base.community.dto.ProjectDto;
import com.base.community.dto.ProjectSkillDto;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectMember;
import com.base.community.model.entity.ProjectSkill;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectMemberRepository;
import com.base.community.model.repository.ProjectRepository;
import com.base.community.model.repository.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.base.community.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectSkillRepository projectSkillRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public Page<Project> getProject(final Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Project getProjectDetail(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
    }

    @Transactional
    public Project createProject(Long memberId, ProjectDto parameter) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 프로젝트 중복 등록 불가
        Optional<Project> projectOptional = projectRepository.findByLeader(member);
        if (projectOptional.isPresent()) {
            throw new CustomException(ALREADY_PROJECT_CREATE);
        }

        // 다른 프로젝트 멤버일 경우 프로젝트 생성 불가
        Optional<ProjectMember> projectMember = projectMemberRepository.findByMember(member);
        if(projectMember.isPresent()) {
            throw new CustomException(ALREADY_PROJECT_REGISTER);
        }

        Project project = projectRepository.save(Project.of(parameter));
        projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .member(member)
                .accept(true)
                .build());
        project.setLeader(member);

        return project;
    }

    @Transactional
    public Project updateProject(Long memberId, ProjectDto parameter) {
        var project = projectRepository.findById(parameter.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        if (!Objects.equals(project.getLeader().getId(), memberId)) { // 작성자만 수정 가능
            throw new CustomException(NOT_UPDATE_VALID_USER);
        }

        if (project.isComplete()) {
            throw new CustomException(ALREADY_PROJECT_COMPLETE_NOT_UPDATE);
        }

        // 현재 프로젝트 신청 인원보다 모집 인원을 적거나 같게 하면 안됨
        if (parameter.getMaxTotal() <= project.getNowTotal()) {
            throw new CustomException(NOT_VALID_MAX_TOTAL);
        }

        project.setTitle(parameter.getTitle());
        project.setContent(parameter.getContent());
        project.setMaxTotal(parameter.getMaxTotal());
        project.setDevelopPeriod(parameter.getDevelopPeriod());
        project.setStartDate(parameter.getStartDate());

        for (ProjectSkillDto dto : parameter.getProjectSkills()) {
            ProjectSkill projectSkill = ProjectSkill.of(dto);
            project.getProjectSkills().add(projectSkill);
        }

        return project;
    }

    public String deleteProject(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_UPDATE_VALID_USER));

        // 프로젝트 생성자 인지 확인
        Project project = projectRepository.findByLeader(member)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
        if (!Objects.equals(project.getId(), projectId)) {
            throw new CustomException(NOT_LEADER_PROJECT);
        }

        // 생성자면 delete 진행
        projectRepository.delete(project);

        return "삭제가 완료되었습니다.";
    }

    public String deleteProjectSkill(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
        projectSkillRepository.deleteByProject(project);
        return "삭제가 완료되었습니다.";
    }

    @Transactional
    public ProjectMember registerProject(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        if (projectMemberRepository.existsByMember(member)) {
            throw new CustomException(ALREADY_PROJECT_REGISTER);
        }
        if (project.isComplete()) {
            throw new CustomException(ALREADY_PROJECT_COMPLETE);
        }
        if (project.getNowTotal() >= project.getMaxTotal()) {
            throw new CustomException(ALREADY_PROJECT_MAX_TOTAL_FULL);
        }

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(member)
                .accept(false)
                .build();

        return projectMemberRepository.save(projectMember);
    }

    @Transactional
    public ProjectMember acceptProject(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        ProjectMember projectMember = projectMemberRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(NOT_VALID_PROJECT_REGISTER_MEMBER));

        projectMember.setAccept(true);
        return projectMember;
    }

    @Transactional(readOnly = true)
    public Project myProjectList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        ProjectMember projectMember = projectMemberRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(NOT_REGISTER_PROJECT));

        if(!projectMember.isAccept()) {
            throw new CustomException(NOT_ACCEPT_PROJECT);
        }

        return projectRepository.findById(projectMember.getProject().getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
    }
}
