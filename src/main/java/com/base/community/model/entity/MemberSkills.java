package com.base.community.model.entity;

import com.base.community.dto.MemberSkillsDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    public static MemberSkills of(MemberSkillsDto form) {
        return MemberSkills.builder()
                .name(form.getName())
                .build();
    }

}
