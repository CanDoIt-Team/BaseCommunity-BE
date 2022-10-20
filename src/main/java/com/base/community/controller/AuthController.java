package com.base.community.controller;


import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import com.base.community.security.TokenProvider;
import com.base.community.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signUp")
    public ResponseEntity<Member> signup(@RequestBody SignUpDto member) {
        Member result = this.memberService.signup(member);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/signup/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(this.memberService.checkEmail(email));
    }

    @GetMapping("/signup/check/nickname")
    public ResponseEntity<Boolean> checkNickName(@RequestParam String nickname) {
        return ResponseEntity.ok(this.memberService.checkNickName(nickname));
    }

    @GetMapping("/signup/email-auth")
    public ResponseEntity<Boolean> emailAuth(@RequestParam String id) {
        return ResponseEntity.ok(this.memberService.emailAuth(id));
    }


    @PostMapping("/signIn/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody ChangePasswordDto form) {
        return ResponseEntity.ok(this.memberService.findPassword(form));
    }


    @PostMapping("/signIn/newPassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody String password,@RequestParam String uuid){
        return ResponseEntity.ok(this.memberService.changePassword(uuid, password));
    }

    @PostMapping("/signIn")
    public  ResponseEntity<?> signIn(@RequestBody SignInDto request){
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider
                .generateToken(member.getEmail(), member.getId());
        return ResponseEntity.ok(token);

    }

    @GetMapping("/Info")
    public ResponseEntity<MemberDto> getInfo(@RequestHeader(name = "auth-token") String token){
        User u = tokenProvider.getUser(token);
        Member m = memberService.findByIdAndEmail(u.getId(),u.getEmail()).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND_USER));
        log.info(u.getEmail()+"<-유저이메일/아이디 ->"+u.getId());
        log.info(m.getEmail());
        return ResponseEntity.ok(MemberDto.from(m));
    }

    @PostMapping("/Info")
    public ResponseEntity<MemberDto> updateInfo(@RequestHeader(name = "auth-token") String token,
                                                  @RequestBody UpdateMemberDto form) {
        User u = tokenProvider.getUser(token);
        form.setId(u.getId());
        return ResponseEntity.ok(MemberDto.from(memberService.updateMember(tokenProvider.getUser(token).getId(),form)));

    }

    @PostMapping("/Info/skill")
    public ResponseEntity<MemberDto> addMemberSkills(@RequestHeader(name = "auth-token") String token,
                                                     @RequestBody AddMemberSkillsDto form){
        return ResponseEntity.ok(MemberDto.from(memberService.addMemberSkills(tokenProvider.getUser(token).getId(),form)));

    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> updateMember(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody InfoChangePasswordDto form) {
        User u = tokenProvider.getUser(token);
        form.setId(u.getId());
        return ResponseEntity.ok(this.memberService.changePassword(form));
    }
}
