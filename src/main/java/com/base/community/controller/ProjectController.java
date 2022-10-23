package com.base.community.controller;

import com.base.community.dto.ProjectDto;
import com.base.community.model.entity.Project;
import com.base.community.security.TokenProvider;
import com.base.community.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<Project> registerProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                      @RequestBody ProjectDto parameter) {
        Project project = projectService.createProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

    @PutMapping
    public ResponseEntity<Project> updateProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody ProjectDto parameter) {
        Project project = projectService.updateProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @PathVariable("projectId") Long projectId) {
        String result = projectService.deleteProject(tokenProvider.getUser(token).getId(), projectId);
        return ResponseEntity.ok(result);
    }
}
