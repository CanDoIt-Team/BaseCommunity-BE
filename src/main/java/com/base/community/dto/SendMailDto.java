package com.base.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendMailDto {
    private String from;
    private String to;
    private String subject;
    private String text;
}
