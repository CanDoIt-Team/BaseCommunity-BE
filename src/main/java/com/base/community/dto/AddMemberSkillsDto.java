package com.base.community.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberSkillsDto {
    private String name;
    private Long memberId;
}