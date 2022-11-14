package com.base.community.service;

import com.base.community.component.MailComponent;
import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import com.base.community.model.repository.MemberRepository;
import com.base.community.security.TokenProvider;
import io.jsonwebtoken.lang.Assert;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private MockMvc mockMvc;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MailComponent mailComponent;


    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setup() {
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());


        Member member = new Member();
        member.setId(99L);
        member.setEmail("test@test.com");
        member.setPassword("test1234");
        member.setNickname("테스트닉네임");
        member.setName("테스트이름");
        member.setBirth(LocalDate.now());
        member.setPhone("01011112222");
        member.setSkills(skill);
        member.setEmailAuthDate(LocalDateTime.now());
        member.setUserStatus(MEMBER_STATUS_ING.getStatus());
    }

    @DisplayName("회원 가입 성공")
    @Test
    void signup_success() {
        //given
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());

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

    @DisplayName("로그인 성공 - 토큰 발행")
    @Test
    void login() {

        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .changePasswordLimitDt(LocalDateTime.now().plusMinutes(30))
                .changePasswordKey("1234")
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));
//        given(memberService.authenticate(SignInDto.builder()
//                .password("password1234")
//                .email(member.getEmail())
//                .build()));
        SignInDto signInDto = new SignInDto("test@test.com", "password1234");
        Member member1 = memberService.authenticate(signInDto);

        String token = tokenProvider.generateToken(member1.getEmail(), member1.getId());

        System.out.println(token);
    }


    @Test
    @DisplayName("비밀번호재설정 - 회원정보찾기 - 성공")
    void find_user_success() {

        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());

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
        given(memberRepository.findByEmailAndName("test@test.com", "테스트")).willReturn(Optional.of(member));
        given(mailComponent.sendEmail(any())).willReturn(true);
        //when
        boolean entity = memberService.findPassword(ChangePasswordDto.builder()
                .email("test@test.com")
                .name("테스트")
                .build());

        //then
        assertTrue(entity);
    }


    @Test
    @DisplayName("비밀번호재설정 -새비밀번호입력- 성공")
    void change_password_success() {


        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());
        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .changePasswordLimitDt(LocalDateTime.now().plusMinutes(30))
                .changePasswordKey("1234")
                .skills(skill)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findByChangePasswordKey(any())).willReturn(Optional.of(member));

        String result = memberService.changePassword("1234", ChangePasswordDto.builder()
                .password("gh1594")
                .build());

        System.out.println(result);
        assertEquals("비밀번호 변경이 완료됐습니다.", result);


    }

    @Test
    @DisplayName("마이페이지 - 회원 정보 조회 - 성공")
    void get_member_info() {
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());

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

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Member result = memberService.getMemberDetail(User.builder()
                .id(1L)
                .email("test@test.com")
                .build());


        assertEquals("멍멍이", result.getNickname());
        assertEquals("테스트", result.getName());
        assertEquals("test@test.com", result.getEmail());
        assertEquals("01012345678", result.getPhone());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 마이페이지 - 성공")
    void change_password_from_mypage() {

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        String result = memberService.changeInfoPassword(1L, InfoChangePasswordDto.builder()
                .newPassword("gh1594")
                .build());


        System.out.println(result);
        assertEquals("비밀번호 변경이 완료됐습니다.", result);
    }

    @Test
    @DisplayName("회원정보 변경 - 유저 정보 변경 - 성공")
    void update_member_info_success() {
        List<MemberSkills> skills = new ArrayList<>();
        skills.add(MemberSkills.builder().name("java").build());
        skills.add(MemberSkills.builder().name("spring").build());

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(skills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Member result = memberService.updateMember(1L, UpdateMemberDto.builder()
                .Nickname("징징이")
                .birth(LocalDate.now().plusDays(3))
                .phone("01099999999")
                .build(), "C++");


        assertEquals("징징이", result.getNickname());
        assertEquals(LocalDate.now().plusDays(3), result.getBirth());
        assertEquals("01099999999",result.getPhone());
    }



    @Test
    @DisplayName("회원탈퇴 -실패")
    void delete_member_success() {
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());
        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("테스트")
                .nickname("멍멍이")
                .birth(LocalDate.now())
                .phone("01012345678")
                .changePasswordLimitDt(LocalDateTime.now().plusMinutes(30))
                .changePasswordKey("1234")
                .skills(skill)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        String result = memberService.deleteMember(1L);

        assertEquals("회원 탈퇴가 완료되었습니다.", result);
    }
}