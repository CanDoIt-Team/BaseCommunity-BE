package com.base.community.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
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
