package com.base.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InfoChangePasswordDto {
    private Long id;
    private String newPassword;
}
