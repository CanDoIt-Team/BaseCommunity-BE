package com.base.community.model.entity;

import com.base.community.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
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

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member leader;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private LocalDate startDate;

    private String developPeriod;

    private Integer maxTotal;

    private Integer nowTotal;

    // 마감 여부
    private boolean isComplete;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @Builder.Default
    private List<ProjectSkill> projectSkills = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @Builder.Default
    private List<ProjectComment> projectComments = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @Builder.Default
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    @Builder.Default
    private List<ChatRoom> chatRooms  = new ArrayList<>();

    public static Project of(ProjectDto dto) {
        return Project.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .maxTotal(dto.getMaxTotal())
                .nowTotal(1)
                .isComplete(false)
                .startDate(dto.getStartDate())
                .developPeriod(dto.getDevelopPeriod())
                .build();
    }
}
