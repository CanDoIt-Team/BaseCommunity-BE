package com.base.community.service;

import com.base.community.component.MailComponent;
import com.base.community.dto.SignUpDto;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.UserSkills;
import com.base.community.model.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MailComponent mailComponent;


    @DisplayName("회원 가입 성공")
    @Test
    void signup_success() {
        //given
        List<UserSkills> skill = new ArrayList<>();
        skill.add(UserSkills.builder().name("java").build());
        skill.add(UserSkills.builder().name("spring").build());

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(skill)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.existsByNickname(any())).willReturn(false);
        given(mailComponent.sendEmail(any())).willReturn(true);
        given(memberRepository.save(any()))
                .willReturn(member);

        //when
        Member entity = memberService.signup(SignUpDto.builder()
                .password("1234")
                .email("thth@thth.com")
                .name("tets")
                .nickname("testse")
                .birth(LocalDate.now())
                .phone("01000000000")
                .build());

        //then
        assertEquals(1L, entity.getId());
        assertEquals("테스트", entity.getName());
        assertEquals("test@test.com", entity.getEmail());
        assertEquals("멍멍이", entity.getNickname());
        assertEquals(LocalDate.now(), entity.getBirth());
        assertEquals("01012345678", entity.getPhone());
        assertEquals("java", entity.getSkills().get(0).getName());
    }

    @DisplayName("회원 가입 실패 - 이미 존재하는 이메일")
    @Test
    void signup_fail_already_exist_email() {
        //given
        given(memberRepository.existsByEmail(any())).willReturn(true);

        //when

        //then
        Assertions.assertThatThrownBy(() -> memberService.signup(SignUpDto.builder()
                        .password("1234")
                        .email("thth@thth.com")
                        .name("test")
                        .nickname("nickname")
                        .birth(LocalDate.now())
                        .phone("01000000000")
                        .build()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_REGISTERED_USER.getMessage());
    }

    @DisplayName("회원 가입 실패 - 이미 존재하는 닉네임")
    @Test
    void signup_fail_already_exist_nickname() {
        //given
        given(memberRepository.existsByNickname(any())).willReturn(true);

        //when

        //then
        Assertions.assertThatThrownBy(() -> memberService.signup(SignUpDto.builder()
                        .password("1234")
                        .email("thth@thth.com")
                        .name("test")
                        .nickname("nickname")
                        .birth(LocalDate.now())
                        .phone("01000000000")
                        .build()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_REGISTERED_NICKNAME.getMessage());
    }
}