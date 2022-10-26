package com.base.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    ALREADY_REGISTERED_NICKNAME(HttpStatus.BAD_REQUEST, "사용중인 닉네임 입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다."),


    NOT_VALID_DATE(HttpStatus.BAD_REQUEST,"유효한 날짜가 아닙니다."),

    NOT_FOUND_BOARD(HttpStatus.BAD_REQUEST, "조회할 게시판 목록이 없습니다."),


    NOT_AUTHORITY_BOARD_MODIFY(HttpStatus.BAD_REQUEST, "게시글을 수정할 권한이 없습니다."),

    NOT_AUTHORITY_BOARD_DELETE(HttpStatus.BAD_REQUEST, "게시글을 삭제할 권한이 없습니다."),
    NOT_AUTHORITY_COMMENT_MODIFY(HttpStatus.BAD_REQUEST, "댓글을 수정할 권한이 없습니다."),
    NOT_AUTHORITY_COMMENT_DELETE(HttpStatus.BAD_REQUEST, "댓글을 삭제할 권한이 없습니다."),

    NOT_FOUND_SKILL(HttpStatus.BAD_REQUEST, "???"),

    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;
}
