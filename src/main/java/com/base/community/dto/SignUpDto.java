package com.base.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phone;
}
