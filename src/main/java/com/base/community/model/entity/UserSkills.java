package com.base.community.model.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static UserSkills of(String name) {
        return UserSkills.builder()
                .name(name)
                .build();
    }
}
