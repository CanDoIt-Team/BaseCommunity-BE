package com.base.community.service;

import com.base.community.dto.ProjectCommentDto;
import com.base.community.exception.CustomException;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectComment;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.ProjectCommentRepository;
import com.base.community.model.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.base.community.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProjectCommentService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCommentRepository projectCommentRepository;

    @Transactional
    public ProjectComment addComment(Long memberId, ProjectCommentDto commentDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Project project = projectRepository.findById(commentDto.getProjectId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        return projectCommentRepository.save(ProjectComment.builder()
                .content(commentDto.getContent())
                .project(project)
                .member(member)
                .build());
    }

    @Transactional
    public ProjectComment updateComment(Long memberId, ProjectCommentDto commentDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        Project project = projectRepository.findById(commentDto.getProjectId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));

        ProjectComment projectComment = projectCommentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT_COMMENT));

        projectComment.setContent(commentDto.getContent());

        return projectComment;
    }

    @Transactional
    public ProjectComment deleteComment(Long projectCommentId) {
        ProjectComment projectComment = projectCommentRepository.findById(projectCommentId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT_COMMENT));

        projectCommentRepository.delete(projectComment);

        return projectComment;
    }
}
