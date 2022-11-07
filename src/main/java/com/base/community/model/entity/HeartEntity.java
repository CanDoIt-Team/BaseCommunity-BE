package com.base.community.model.entity;


import lombok.*;


import javax.persistence.*;
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
    private Long  boardId;
}
