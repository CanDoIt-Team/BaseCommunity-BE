package com.base.community.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePasswordDto {
    private String email;
    private String name;
    private String password;
}
