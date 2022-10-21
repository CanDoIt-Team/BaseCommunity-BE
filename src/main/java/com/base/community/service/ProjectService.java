package com.base.community.service;

import com.base.community.dto.ProjectDto;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectRepository;
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

    @Transactional
    public Project createProject(Long memberId, ProjectDto parameter) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Optional<Project> projectOptional = projectRepository.findByLeader(member);
        if (projectOptional.isPresent()) {
            throw new CustomException(ALREADY_PROJECT_CREATE);
        }

        Project project = projectRepository.save(Project.of(parameter));
        project.setLeader(member);

        return project;
    }

    @Transactional
    public Project updateProject(Long memberId, ProjectDto parameter) {
        var project = projectRepository.findById(parameter.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        if (!Objects.equals(project.getLeader().getId(), memberId)) { // 작성자만 수정 가능
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

        return project;
    }
}
