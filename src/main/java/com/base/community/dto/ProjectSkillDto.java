package com.base.community.dto;

import com.base.community.model.entity.Project;
import com.base.community.model.entity.ProjectSkill;
import lombok.*;


public class ProjectSkillDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long id;
        private String name;
        private Project project;

        public ProjectSkill toEntity() {
            ProjectSkill projectSkill = ProjectSkill.builder().id(id).name(name).project(project).build();

            return projectSkill;
        }
    }
    @Getter
    public static class Response {
        private Long id;
        private String name;
        private Long projectId;

        public Response(ProjectSkill projectSkill) {
            this.id = projectSkill.getId();
            this.name = projectSkill.getName();
            this.projectId= projectSkill.getProject().getId();
        }
    }

}
