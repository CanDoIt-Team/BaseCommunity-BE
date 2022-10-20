package com.base.community.model.entity;

import com.base.community.dto.SignUpDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Column(unique = true)
    private String nickname;

    private String name;

    private LocalDate birth;

    private String phone;

    private String userStatus;
    private boolean emailAuth;
    private LocalDateTime emailAuthDate;
    private String emailAuthKey;
    private String changePasswordKey;
    private LocalDateTime changePasswordLimitDt;

    private String filename; //파일이름
    private String urlFilename;  //파일주소


    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private List<MemberSkills> skills = new ArrayList<>();

    public static Member from(SignUpDto dto) {
        return Member.builder()
                .email(dto.getEmail().toLowerCase(Locale.ROOT))
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .birth(dto.getBirth())
                .phone(dto.getPhone())
                .userStatus(MEMBER_STATUS_REQ.getStatus())
                .emailAuth(false)
                .build();
    }
}
