package com.base.community.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberDto {
    private Long id;
    private LocalDate birth;
    private String phone;
    private List<AddMemberSkillsDto> skills;
}
