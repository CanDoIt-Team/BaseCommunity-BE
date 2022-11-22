package com.base.community.controller;


import com.base.community.dto.*;
import com.base.community.model.entity.Member;
import com.base.community.security.TokenProvider;
import com.base.community.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;


    @ApiOperation(value = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<MemberDto> signup(@RequestBody SignUpDto member) {
        return ResponseEntity.ok(this.memberService.signup(member));
    }

    @ApiOperation(value = "회원가입 - 이메일 체크")
    @GetMapping("/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(this.memberService.checkEmail(email));
    }

    @ApiOperation(value = "회원가입 - 닉네임 체크")
    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNickName(@RequestParam String nickname) {
        return ResponseEntity.ok(this.memberService.checkNickName(nickname));
    }

    @ApiOperation(value = "회원가입 - 이메일 인증")
    @GetMapping("/email-auth")
    public ResponseEntity<Boolean> emailAuth(@RequestParam String id) {
        return ResponseEntity.ok(this.memberService.emailAuth(id));
    }

    @ApiOperation(value = "로그인 페이지 - 비밀번호 변경(회원정보 입력)")
    @PostMapping("/password/user-info")
    public ResponseEntity<Boolean> findPassword(@RequestBody ChangePasswordDto form) {


        return ResponseEntity.ok(this.memberService.findPassword(form));
    }

    @ApiOperation(value = "로그인 페이지 - 비밀번호 변경(새 비밀번호 입력)")
    @PostMapping("/password/new")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto form, @RequestParam String uuid) {

        String result = memberService.changePassword(uuid, form);
        return ResponseEntity.ok(result);
    }


    @ApiOperation(value = "로그인")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto request) {
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider
                .generateToken(member.getEmail(), member.getId());
        return ResponseEntity.ok(token);

    }

    @ApiOperation(value = "마이페이지 - 회원정보")
    @GetMapping("/info")
    public ResponseEntity<MemberDto> getInfo(@RequestHeader(name = "auth-token") String token) {
        return ResponseEntity.ok(memberService.getMemberDetail(tokenProvider.getUser(token)));
    }

    @ApiOperation(value = "마이페에지 - 회원정보 수정")
    @PostMapping("/info")
    public ResponseEntity<MemberDto> updateInfo(@RequestHeader(name = "auth-token") String token,
                                             @RequestBody UpdateMemberDto form, @RequestParam(value = "skill", required = false) String skill) {
        log.info(skill);
        return ResponseEntity.ok(memberService.updateMember(tokenProvider.getUser(token).getId(), form, skill));
    }

    @ApiOperation(value = "마이페이지 - 비밀번호 변경")
    @PutMapping("/password/change")
    public ResponseEntity<String> updateMember(@RequestHeader(name = "auth-token") String token,
                                               @RequestBody InfoChangePasswordDto form) {

        String result = memberService.changeInfoPassword(tokenProvider.getUser(token).getId(), form);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "마이페이지 - 프로필 이미지 수정")
    @PostMapping("/profile-img")
    public ResponseEntity<MemberDto> uploadProfileImg(@RequestHeader(name = "auth-token") String token,
                                                   @RequestPart MultipartFile file) {
        return ResponseEntity.ok(memberService.uploadProfileImg(tokenProvider.getUser(token).getId(), file));

    }

    @ApiOperation(value = "마이페이지 - 회원탈퇴")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> deleteMember(@RequestHeader(name = "auth-token", required = false) String token) {
        String result = memberService.deleteMember(tokenProvider.getUser(token).getId());
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "로그아웃")
    @GetMapping("/signout")
        public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:/";
    }

}
