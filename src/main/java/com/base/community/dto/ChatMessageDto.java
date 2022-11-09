package com.base.community.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {

    private Long memberId;
    private String message;
    private LocalDateTime sendTime;
}