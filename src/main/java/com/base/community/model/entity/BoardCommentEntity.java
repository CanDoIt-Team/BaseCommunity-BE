package com.base.community.model.entity;


import com.base.community.dto.BoardCommentDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "BOARD_COMMENT")
@AuditOverride(forClass = BaseEntity.class)
public class BoardCommentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    private String content;

    public static BoardCommentEntity from(
            BoardCommentDto dto , Member member, BoardEntity boardEntity){

        return BoardCommentEntity.builder()
                .member(member)
                .boardEntity(boardEntity)
                .content(dto.getContent())
                .build();

    }


}
