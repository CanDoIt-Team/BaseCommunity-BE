package com.base.community.dto;


import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phone;
    private List<MemberSkills> skills;

 public static MemberDto from(Member member){
     return new MemberDto(member.getEmail(), member.getName(), member.getNickname(), member.getBirth(),member.getPhone(),member.getSkills());
 }
}
