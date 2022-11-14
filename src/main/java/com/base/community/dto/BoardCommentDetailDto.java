package com.base.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCommentDetailDto {

    private Long boardId;
    private Long commentId;
    private Long memberId;
    private String nickname;
    private String urlFilename;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
