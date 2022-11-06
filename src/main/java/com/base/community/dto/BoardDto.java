package com.base.community.dto;


import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {

    private String category;
    private String title;
    private String content;

}

