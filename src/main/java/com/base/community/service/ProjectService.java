package com.base.community.service;

import com.base.community.dto.ProjectDto;
import com.base.community.dto.ProjectSkillDto;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectSkill;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectRepository;
import com.base.community.model.repository.ProjectSkillRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Project createProject(Long memberId, ProjectDto parameter) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Optional<Project> projectOptional = projectRepository.findByLeaderId(memberId);
        if (projectOptional.isPresent()) {
            throw new CustomException(ALREADY_PROJECT_CREATE);
        }

        Project project = projectRepository.save(Project.of(parameter));
        project.setLeaderId(memberId);

        return project;
    }

    @Transactional
    public Project updateProject(Long memberId, ProjectDto parameter) {
        var project = projectRepository.findById(parameter.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        if (!Objects.equals(project.getLeaderId(), memberId)) { // 작성자만 수정 가능
            throw new CustomException(NOT_VALID_USER);
        }

        if (project.isComplete()) { // 마감한 프로젝트는 수정 못함
            throw new CustomException(ALREADY_PROJECT_COMPLETE);
        }

        // 현재 프로젝트 신청 인원보다 모집 인원을 적거나 같게 하면 안됨
        if (parameter.getMaxTotal() <= project.getNowTotal()) {
            throw new CustomException(NOT_VALID_MAX_TOTAL);
        }

        project.setTitle(parameter.getTitle());
        project.setContent(parameter.getContent());
        project.setMaxTotal(parameter.getMaxTotal());

        // 해당 유저에 맞는 프로젝트 스킬 다 삭제 후 다시 넣음
        Iterable<ProjectSkill> projectSkills = projectSkillRepository.findByProject(project);
        for (ProjectSkill skill: projectSkills) {
            projectSkillRepository.deleteById(skill.getId());
        }

        for (ProjectSkillDto dto: parameter.getProjectSkills()) {
            ProjectSkill projectSkill = ProjectSkill.of(dto);
            project.getProjectSkills().add(projectSkill);
        }

        return project;
    }

    @Transactional
    public String deleteProject(Long memberId, Long projectId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_VALID_USER));

        // 프로젝트 생성자 인지 확인
        Project project = projectRepository.findByLeaderId(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
        if (!Objects.equals(project.getId(), projectId)) {
            throw new CustomException(NOT_LEADER_PROJECT);
        }

        // 생성자면 delete 진행
        projectRepository.delete(project);

        return "삭제가 완료되었습니다.";
    }
}
