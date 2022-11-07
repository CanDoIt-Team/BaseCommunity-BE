package com.base.community.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDetailDto {

    private Long boardId;
    private String category;
    private String title;
    private String nickname;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private List<BoardCommentDetailDto> comments;
}
