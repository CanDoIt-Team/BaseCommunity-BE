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
    NOT_FOUND_SKILL(HttpStatus.BAD_REQUEST, "스킬이 존재하지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_AUTHENTICATE_USER(HttpStatus.BAD_REQUEST,"이메일 인증이 필요합니다."),
    WITHDRAW_USER(HttpStatus.BAD_REQUEST,"탈퇴한 회웝입니다."),
    NOT_VALID_USER(HttpStatus.BAD_REQUEST, "유효하지 않은 회원입니다."),

    ALREADY_PROJECT_CREATE(HttpStatus.BAD_REQUEST, "프로젝트를 이미 등록하였습니다."),

    ALREADY_PROJECT_MAX_TOTAL_FULL(HttpStatus.BAD_REQUEST, "모집 정원을 초과하였습니다."),

    ALREADY_PROJECT_RECRUIT_END(HttpStatus.BAD_REQUEST, "이미 프로젝트 모집을 완료 하였습니다."),
    NOT_UPDATE_VALID_USER(HttpStatus.BAD_REQUEST, "업데이트 권한이 없는 회원 입니다."),
    ALREADY_PROJECT_COMPLETE_NOT_UPDATE(HttpStatus.BAD_REQUEST, "마감한 프로젝트는 수정할 수 없습니다."),
    ALREADY_PROJECT_COMPLETE(HttpStatus.BAD_REQUEST, "마감된 프로젝트 입니다."),
    NOT_VALID_MAX_TOTAL(HttpStatus.BAD_REQUEST, "현재 신청한 인원보다 모집 인원의 수가 더 많아야 합니다."),
    NOT_FOUND_PROJECT(HttpStatus.BAD_REQUEST, "일치하는 프로젝트가 없습니다."),
    NOT_LEADER_PROJECT(HttpStatus.BAD_REQUEST, "프로젝트 생성자가 아닙니다."),
    NOT_FOUND_PROJECT_COMMENT(HttpStatus.BAD_REQUEST, "프로젝트 댓글이 존재하지 않습니다."),

    NOT_VALID_PROJECT_REGISTER_MEMBER(HttpStatus.BAD_REQUEST, "프로젝트 신청 멤버가 아닙니다."),
    ALREADY_PROJECT_REGISTER(HttpStatus.BAD_REQUEST, "이미 프로젝트를 신청하였습니다."),
    NOT_REGISTER_PROJECT(HttpStatus.BAD_REQUEST, "프로젝트 신청 내역이 없습니다."),
    NOT_ACCEPT_PROJECT(HttpStatus.BAD_REQUEST, "신청한 프로젝트가 아직 수락되지 않았습니다."),
    NOT_FOUND_USER_IN_PROJECT_MEMBER(HttpStatus.BAD_REQUEST, "프로젝트 멤버에 회원이 존재하지 않습니다."),
    ALREADY_PROJECT_START(HttpStatus.BAD_REQUEST, "이미 프로젝트가 시작되어 당신은 도망칠 수 없습니다."),

    NOT_FOUND_BOARD_COMMENT(HttpStatus.BAD_REQUEST, "게시글 댓글이 존재하지 않습니다."),
    NOT_FOUND_BOARD(HttpStatus.BAD_REQUEST, "조회할 게시글이 없습니다."),

    NOT_AUTHORITY_BOARD_MODIFY(HttpStatus.BAD_REQUEST, "게시글을 수정할 권한이 없습니다."),
    NOT_AUTHORITY_BOARD_DELETE(HttpStatus.BAD_REQUEST, "게시글을 삭제할 권한이 없습니다."),
    NOT_AUTHORITY_COMMENT_MODIFY(HttpStatus.BAD_REQUEST, "댓글을 수정할 권한이 없습니다."),
    NOT_AUTHORITY_COMMENT_DELETE(HttpStatus.BAD_REQUEST, "댓글을 삭제할 권한이 없습니다."),

    NOT_FOUND_CHATROOM(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다."),
    NOT_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효한 토큰이 아닙니다.")
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
