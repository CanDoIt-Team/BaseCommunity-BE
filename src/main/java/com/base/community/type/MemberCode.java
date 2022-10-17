package com.base.community.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberCode {

    MEMBER_STATUS_REQ("REQ", "요청중"),
    MEMBER_STATUS_ING("ING", "이용중"),
    MEMBER_STATUS_STOP("WITHDRAW", "탈퇴");

    private final String status;
    private final String description;
}
