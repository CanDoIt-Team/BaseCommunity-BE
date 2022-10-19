package com.base.community.controller;

import com.base.community.dto.ChangePasswordDto;
import com.base.community.dto.SignInDto;
import com.base.community.dto.SignUpDto;
import com.base.community.model.entity.Member;
import com.base.community.security.TokenProvider;
import com.base.community.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
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


    @PostMapping("/findPassword")
    public ResponseEntity<Boolean> findPassword(@RequestBody ChangePasswordDto form) {
        return ResponseEntity.ok(this.memberService.findPassword(form));
    }


    @PostMapping("/newpassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody String password,@RequestParam String uuid){
        return ResponseEntity.ok(this.memberService.changePassword(uuid, password));
    }

    @PostMapping("/signin")
    public  ResponseEntity<?> signIn(@RequestBody SignInDto request){
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider
                .generateToken(member.getEmail(), member.getId());
        return ResponseEntity.ok(token);

    }
}
