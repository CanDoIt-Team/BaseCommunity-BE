package com.base.community.model.entity;


import com.base.community.dto.BoardDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity(name = "BOARD")
@AllArgsConstructor
@NoArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class BoardEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    @Builder.Default
    private List<BoardCommentEntity> comments = new ArrayList<>();

    public static BoardEntity from(BoardDto dto , Member member) {
        return BoardEntity.builder()
                .member(member)
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }

}
