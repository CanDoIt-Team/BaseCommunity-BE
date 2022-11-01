package com.base.community.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListResDto {

    private Long boardId;
    private String category;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;


}
