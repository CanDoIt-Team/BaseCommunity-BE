package com.base.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCommentDto {
    private Long id;
    private Long projectId;
    private String content;
}
