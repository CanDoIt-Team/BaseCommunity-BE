package com.base.community.model.entity;

import com.base.community.dto.ProjectSkillDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public static ProjectSkill of(ProjectSkillDto form) {
        return ProjectSkill.builder()
                .name(form.getName())
                .build();
    }
}
