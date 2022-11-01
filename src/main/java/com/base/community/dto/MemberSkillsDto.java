package com.base.community.dto;

import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import lombok.*;


public class MemberSkillsDto {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long id;
        private String name;
        private Member member;

        public MemberSkills toEntity() {
            MemberSkills memberSkills = MemberSkills.builder().id(id).name(name).member(member).build();

            return memberSkills;
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private String name;
        private Long memberId;

        public Response(MemberSkills memberSkills) {
            this.id = memberSkills.getId();
            this.name = memberSkills.getName();
            this.memberId = memberSkills.getMember().getId();
        }
    }


}
