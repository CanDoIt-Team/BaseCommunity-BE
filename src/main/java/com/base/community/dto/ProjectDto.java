package com.base.community.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ProjectDto {
    private Long id;
    private String title;
    private String content;
    private Integer maxTotal;
    private LocalDate startDate;
    private String developPeriod;
    private List<ProjectSkillDto> projectSkills;
}