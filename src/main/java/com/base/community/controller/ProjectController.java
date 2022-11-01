package com.base.community.controller;

import com.base.community.dto.ProjectCommentDto;
import com.base.community.dto.ProjectDto;
import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectComment;
import com.base.community.security.TokenProvider;
import com.base.community.service.ProjectCommentService;
import com.base.community.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCommentService projectCommentService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<?> showProject(final Pageable pageable) {
        Page<Project> projects = projectService.getProject(pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> showProjectDetail(@PathVariable("projectId") Long projectId) {
        Project project = projectService.getProjectDetail(projectId);
        return ResponseEntity.ok(project);
    }

    @PutMapping
    public ResponseEntity<Project> updateProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ProjectDto parameter) {
        Project project = projectService.updateProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<Project> registerProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                      @RequestBody ProjectDto parameter) {
        Project project = projectService.createProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(projectService.deleteProject(tokenProvider.getUser(token).getId(), projectId));
    }

    @DeleteMapping("/{projectId}/skill")
    public ResponseEntity<String> deleteProjectSkill(@PathVariable("projectId") Long projectId) {
        String result = projectService.deleteProjectSkill(projectId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/comments")
    public ResponseEntity<ProjectComment> addComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                     @RequestBody ProjectCommentDto projectComment) {
        return ResponseEntity.ok(
                projectCommentService.addComment(tokenProvider.getUser(token).getId(), projectComment));
    }

    @PutMapping("/{projectId}/comments")
    public ResponseEntity<ProjectComment> updateComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody ProjectCommentDto projectComment) {
        return ResponseEntity.ok(projectCommentService.updateComment(tokenProvider.getUser(token).getId(), projectComment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        projectCommentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}
