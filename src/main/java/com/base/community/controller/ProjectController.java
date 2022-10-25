package com.base.community.controller;

import com.base.community.dto.ProjectDto;
import com.base.community.model.entity.Project;
import com.base.community.security.TokenProvider;
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
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<?> showProject(final Pageable pageable) {
        Page<Project> projects = projectService.getProject(pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> showProjectDetail(@PathVariable("projectId") Long projectId) {
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
    public ResponseEntity<?> deleteProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @PathVariable("projectId") Long projectId) {
        String result = projectService.deleteProject(tokenProvider.getUser(token).getId(), projectId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{projectId}/skill")
    public ResponseEntity.BodyBuilder deleteProjectSkill(@PathVariable("projectId") Long projectId) {
        projectService.deleteProjectSkill(projectId);
        return ResponseEntity.ok();
    }
}
