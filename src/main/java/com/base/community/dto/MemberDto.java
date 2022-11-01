package com.base.community.dto;


import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phone;
    private String filename; //파일이름
    private String urlFilename;  //파일주소
    private List<MemberSkillsDto> skills;



}
