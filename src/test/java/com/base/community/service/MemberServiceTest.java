package com.base.community.service;

import com.base.community.component.MailComponent;
import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import com.base.community.model.repository.MemberRepository;
import com.base.community.security.TokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

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

    @Mock
    private ModelMapper modelMapper;


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
        member.setNickname("??????????????????");
        member.setName("???????????????");
        member.setBirth(LocalDate.now());
        member.setPhone("01011112222");
        member.setSkills(skill);
        member.setEmailAuthDate(LocalDateTime.now());
        member.setUserStatus(MEMBER_STATUS_ING.getStatus());
    }

    @DisplayName("?????? ?????? ??????")
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
                .name("?????????")
                .nickname("?????????")
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
        given(memberRepository.save(any())).willReturn(member);
        given(modelMapper.map(any(), any())).willReturn(MemberDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now())
                .phone("01012345678")
                .build());

        //when
        MemberDto memberDto = memberService.signup(SignUpDto.builder()
                .password("1234")
                .email("thth@thth.com")
                .name("tets")
                .nickname("testse")
                .birth(LocalDate.now())
                .phone("01000000000")
                .build());

        //then
        assertEquals(1L, memberDto.getId());
        assertEquals("?????????", memberDto.getName());
        assertEquals("test@test.com", memberDto.getEmail());
        assertEquals("?????????", memberDto.getNickname());
        assertEquals(LocalDate.now(), memberDto.getBirth());
        assertEquals("01012345678", memberDto.getPhone());
    }

    @DisplayName("?????? ?????? ?????? - ?????? ???????????? ?????????")
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

    @DisplayName("?????? ?????? ?????? - ?????? ???????????? ?????????")
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

    @DisplayName("????????? ?????? - ?????? ??????")
    @Test
    void login() {

        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("?????????")
                .nickname("?????????")
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
    @DisplayName("????????????????????? - ?????????????????? - ??????")
    void find_user_success() {

        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(skill)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();
        given(memberRepository.findByEmailAndName("test@test.com", "?????????")).willReturn(Optional.of(member));
        given(mailComponent.sendEmail(any())).willReturn(true);
        //when
        boolean entity = memberService.findPassword(ChangePasswordDto.builder()
                .email("test@test.com")
                .name("?????????")
                .build());

        //then
        assertTrue(entity);
    }


    @Test
    @DisplayName("????????????????????? -?????????????????????- ??????")
    void change_password_success() {


        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());
        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("?????????")
                .nickname("?????????")
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
        assertEquals("???????????? ????????? ??????????????????.", result);


    }

    @Test
    @DisplayName("??????????????? - ?????? ?????? ?????? - ??????")
    void get_member_info() {
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(skill)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(modelMapper.map(any(), any())).willReturn(MemberDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now())
                .phone("01012345678")
                .build());

        MemberDto result = memberService.getMemberDetail(User.builder()
                .id(1L)
                .email("test@test.com")
                .build());


        assertEquals("?????????", result.getNickname());
        assertEquals("?????????", result.getName());
        assertEquals("test@test.com", result.getEmail());
        assertEquals("01012345678", result.getPhone());
    }

    @Test
    @DisplayName("???????????? ????????? - ??????????????? - ??????")
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
        assertEquals("???????????? ????????? ??????????????????.", result);
    }

    @Test
    @DisplayName("???????????? ?????? - ?????? ?????? ?????? - ??????")
    void update_member_info_success() {
        List<MemberSkills> skills = new ArrayList<>();
        skills.add(MemberSkills.builder().name("java").build());
        skills.add(MemberSkills.builder().name("spring").build());

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("1234")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now())
                .phone("01012345678")
                .skills(skills)
                .emailAuth(true)
                .emailAuthDate(LocalDateTime.now())
                .userStatus(MEMBER_STATUS_ING.getStatus())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(modelMapper.map(any(), any())).willReturn(MemberDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("?????????")
                .nickname("?????????")
                .birth(LocalDate.now().plusDays(3))
                .phone("01099999999")
                .build());

        MemberDto result = memberService.updateMember(1L, UpdateMemberDto.builder()
                .Nickname("?????????")
                .birth(LocalDate.now().plusDays(3))
                .phone("01099999999")
                .build(), "C++");


        assertEquals("?????????", result.getNickname());
        assertEquals(LocalDate.now().plusDays(3), result.getBirth());
        assertEquals("01099999999", result.getPhone());
    }


    @Test
    @DisplayName("???????????? -??????")
    void delete_member_success() {
        List<MemberSkills> skill = new ArrayList<>();
        skill.add(MemberSkills.builder().name("java").build());
        skill.add(MemberSkills.builder().name("spring").build());
        String encPassword = bCryptPasswordEncoder.encode("password1234");

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .password(encPassword)
                .name("?????????")
                .nickname("?????????")
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

        assertEquals("?????? ????????? ?????????????????????.", result);
    }
}