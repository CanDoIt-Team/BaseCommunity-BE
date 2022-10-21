package com.base.community.model.entity;

import com.base.community.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Entity(name = "PROJECT")
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Project extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member leader;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private Integer maxTotal;

    private Integer nowTotal;

    // 마감 여부
    private boolean isComplete;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    public static Project of(ProjectDto dto) {
        return Project.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .maxTotal(dto.getMaxTotal())
                .projectSkills(dto.getProjectSkills().stream()
                        .map(pjForm -> ProjectSkill.of(pjForm))
                        .collect(Collectors.toList()))
                .nowTotal(0)
                .isComplete(false)
                .build();
    }
}
