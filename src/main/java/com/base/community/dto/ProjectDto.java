package com.base.community.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProjectDto {
    private Long id;
    private String title;
    private String content;
    private Integer maxTotal;
    private List<ProjectSkillDto> projectSkills;
}