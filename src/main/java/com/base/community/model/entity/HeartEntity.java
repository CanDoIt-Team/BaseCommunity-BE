package com.base.community.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Getter
@Builder
@Entity(name = "BOARD_HEART")
@AllArgsConstructor
@NoArgsConstructor
@IdClass(HeartId.class)
public class HeartEntity implements Serializable {

    @Id
    private Long memberId;

    @Id
    private Long boardId;
}
