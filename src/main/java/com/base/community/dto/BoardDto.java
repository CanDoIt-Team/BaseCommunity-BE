package com.base.community.dto;


import com.base.community.model.entity.BoardEntity;
import com.base.community.model.entity.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {

    private String category;
    private String title;
    private String content;

}

