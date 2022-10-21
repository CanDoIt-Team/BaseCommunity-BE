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
    public ResponseEntity<?> registerProject(@RequestHeader(name = "X-AUTH-TOCKEN") String token,
                                      @RequestBody ProjectDto parameter) {
        Project project = projectService.createProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

    @PutMapping
    public ResponseEntity<?> updateProject(@RequestHeader(name = "X-AUTH-TOCKEN") String token,
                                           @RequestBody ProjectDto parameter) {
        Project project = projectService.updateProject(tokenProvider.getUser(token).getId(), parameter);
        return ResponseEntity.ok(project);
    }

}
