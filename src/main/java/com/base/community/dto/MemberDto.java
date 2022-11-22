package com.base.community.dto;


import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phone;
    private String filename; //파일이름
    private String urlFilename;  //파일주소
    private List<MemberSkillsDto> skills;

}
