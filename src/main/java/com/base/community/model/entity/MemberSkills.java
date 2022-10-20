package com.base.community.model.entity;

import com.base.community.dto.AddMemberSkillsDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSkills extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static MemberSkills of(List<String> name) {
        return MemberSkills.builder()
                .name(String.valueOf(name))
                .build();
    }

}
