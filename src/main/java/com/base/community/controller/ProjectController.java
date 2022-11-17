package com.base.community.controller;

import com.base.community.dto.ProjectCommentDto;
import com.base.community.dto.ProjectDto;
import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectComment;
import com.base.community.model.entity.ProjectMember;
import com.base.community.security.TokenProvider;
import com.base.community.service.ProjectCommentService;
import com.base.community.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCommentService projectCommentService;
    private final TokenProvider tokenProvider;

    @ApiOperation(value = "프로젝트 조회")
    @GetMapping
    public ResponseEntity<?> showProject(final Pageable pageable,
                                         @RequestParam(required = false) String keyword) {
        Page<Project> projects = projectService.getProject(pageable, keyword);
        return ResponseEntity.ok(projects);
    }

    @ApiOperation(value = "프로젝트 상세 보기")
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> showProjectDetail(@PathVariable("projectId") Long projectId) {
        Project project = projectService.getProjectDetail(projectId);
        return ResponseEntity.ok(project);
    }

    @ApiOperation(value = "프로젝트 수정")
    @PutMapping
    public ResponseEntity<Project> updateProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ProjectDto parameter,  @RequestParam(value = "skill", required = false) String skill) {
        Project project = projectService.updateProject(tokenProvider.getUser(token).getId(), parameter, skill);
        return ResponseEntity.ok(project);
    }

    @ApiOperation(value = "프로젝트 등록")
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                      @RequestBody ProjectDto parameter, @RequestParam(value = "skill", required = false) String skill) {
        Project project = projectService.createProject(tokenProvider.getUser(token).getId(), parameter, skill);
        return ResponseEntity.ok(project);
    }

    @ApiOperation(value = "프로젝트 삭제", notes = "프로젝트 스킬 삭제 먼저하고 프로젝트 삭제")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(projectService.deleteProject(tokenProvider.getUser(token).getId(), projectId));
    }

//    @ApiOperation(value = "프로젝트 스킬 삭제")
//    @DeleteMapping("/{projectId}/skill")
//    public ResponseEntity<String> deleteProjectSkill(@PathVariable("projectId") Long projectId) {
//        String result = projectService.deleteProjectSkill(projectId);
//        return ResponseEntity.ok(result);
//    }

    @ApiOperation(value = "프로젝트 신청")
    @GetMapping("/register/{projectId}")
    public ResponseEntity<ProjectMember> registerProjectMember(@RequestHeader("X-AUTH-TOKEN") String token,
                                             @PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(projectService
                .registerProjectMember(tokenProvider.getUser(token).getId(), projectId));
    }

    @ApiOperation(value = "프로젝트 수락")
    @GetMapping("/accept/{memberId}")
    public ResponseEntity<?> acceptProjectMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(projectService.acceptProjectMember(memberId));
    }

    @ApiOperation(value = "프로젝트 댓글 등록")
    @PostMapping("/{projectId}/comments")
    public ResponseEntity<ProjectComment> addComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                     @RequestBody ProjectCommentDto projectComment) {
        return ResponseEntity.ok(
                projectCommentService.addComment(tokenProvider.getUser(token).getId(), projectComment));
    }

    @ApiOperation(value = "프로젝트 댓글 수정")
    @PutMapping("/{projectId}/comments")
    public ResponseEntity<ProjectComment> updateComment(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                           @RequestBody ProjectCommentDto projectComment) {
        return ResponseEntity.ok(projectCommentService.updateComment(tokenProvider.getUser(token).getId(), projectComment));
    }

    @ApiOperation(value = "프로젝트 댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        projectCommentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    @ApiOperation(value = "내 프로젝트 조회")
    @GetMapping("/myProjectList")
    public ResponseEntity<Project> myProjectList(@RequestHeader(name = "X-AUTH-TOKEN") String token) {
        return ResponseEntity.ok(projectService.myProjectList(tokenProvider
                .getUser(token).getId()));
    }
}
