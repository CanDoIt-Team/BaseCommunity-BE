package com.base.community.model.entity;

import com.base.community.dto.SignUpDto;
import com.base.community.type.MemberCode;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.base.community.type.MemberCode.MEMBER_STATUS_REQ;

@Getter
@Setter
@Builder
@Entity(name = "MEMBER")
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Member extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String name;

    private String password;

    private LocalDate birth;

    private String phone;

    private String userStatus;
    private boolean emailAuth;
    private LocalDateTime emailAuthDate;
    private String emailAuthKey;
    private String changePasswordKey;
    private LocalDateTime changePasswordLimitDt;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    public static Member from(SignUpDto dto, String uuid) {
        return Member.builder()
                .email(dto.getEmail().toLowerCase(Locale.ROOT))
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .birth(dto.getBirth())
                .phone(dto.getPhone())
                .skills(dto.getSkills().stream()
                        .map(skillName -> Skill.of(skillName))
                        .collect(Collectors.toList()))
                .userStatus(MEMBER_STATUS_REQ.getStatus())
                .emailAuth(false)
                .emailAuthKey(uuid)
                .build();
    }
}
