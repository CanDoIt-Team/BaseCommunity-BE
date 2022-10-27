package com.base.community.controller;


import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.security.TokenProvider;
import com.base.community.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

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

    @GetMapping("/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(this.memberService.checkEmail(email));
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNickName(@RequestParam String nickname) {
        return ResponseEntity.ok(this.memberService.checkNickName(nickname));
    }

    @GetMapping("/email-auth")
    public ResponseEntity<Boolean> emailAuth(@RequestParam String id) {
        return ResponseEntity.ok(this.memberService.emailAuth(id));
    }

    //로그인페이지 - 비밀번호변경(회원정보 입력)
    @PostMapping("/password/user-info")
    public ResponseEntity<Boolean> findPassword(@RequestBody ChangePasswordDto form) {


        return ResponseEntity.ok(this.memberService.findPassword(form));
    }

    //로그인페이지 - 비밀번호변경(새 비밀번호 입력)
    @PostMapping("/password/new")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto form,@RequestParam String uuid){

        String result =  memberService.changePassword(uuid, form);
        return ResponseEntity.ok(result);
    }



    @PostMapping("/signin")
    public  ResponseEntity<?> signIn(@RequestBody SignInDto request){
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider
                .generateToken(member.getEmail(), member.getId());
        return ResponseEntity.ok(token);

    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@RequestHeader(name = "auth-token") String token){
        Member member = memberService.getMemberDetail(tokenProvider.getUser(token));
        return ResponseEntity.ok(member);
    }

    @PostMapping("/info")
    public ResponseEntity<Member> updateInfo(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody MemberDto form) {
        Member member = memberService.updateMember(tokenProvider.getUser(token).getId(),form);
        return ResponseEntity.ok(member);
    }


    //인포페이지 비밀번호 변경
    @PutMapping("/password/change")
    public ResponseEntity<String> updateMember(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody InfoChangePasswordDto form) {

        String result = memberService.changeInfoPassword(tokenProvider.getUser(token).getId(), form);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/info")
    public ResponseEntity<String> deleteSkill(@RequestHeader(name = "auth-token") String token,
                                            @RequestParam Long id) {
        String result = memberService.deleteSkill(tokenProvider.getUser(token).getId(), id);

        return ResponseEntity.ok(result);
    }


    @PostMapping("/profile-img")
    public ResponseEntity<Member> uploadProfileImg(@RequestHeader(name = "auth-token") String token,
                                                      @RequestPart MultipartFile file){
        Member member = memberService.uploadProfileImg(tokenProvider.getUser(token).getId(),file);

        return ResponseEntity.ok(member);

    }


    @DeleteMapping("/withdraw")
    public ResponseEntity<String> deleteMember(@RequestHeader(name = "auth-token", required = false) String token) {
        String result = memberService.deleteMember(tokenProvider.getUser(token).getId());

        return ResponseEntity.ok(result);
    }


    @GetMapping("/signout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

}
