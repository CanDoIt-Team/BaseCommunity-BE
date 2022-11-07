package com.base.community.dto;

import lombok.*;

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
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
