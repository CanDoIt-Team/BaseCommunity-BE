package com.base.community.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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


    public static MemberSkills of(String form) {
        return MemberSkills.builder()
                .name(form)
                .build();
    }


}
