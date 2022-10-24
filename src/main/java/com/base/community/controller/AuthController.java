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


    @PostMapping("/password/user-info")
    public ResponseEntity<Boolean> findPassword(@RequestBody ChangePasswordDto form) {
        return ResponseEntity.ok(this.memberService.findPassword(form));
    }


    @PostMapping("/password/new")
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordDto form,@RequestParam String uuid){
        return ResponseEntity.ok(this.memberService.changePassword(uuid, form));
    }

    @PostMapping("/signin")
    public  ResponseEntity<?> signIn(@RequestBody SignInDto request){
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider
                .generateToken(member.getEmail(), member.getId());
        return ResponseEntity.ok(token);

    }

    @GetMapping("/info")
    public ResponseEntity<MemberDto> getInfo(@RequestHeader(name = "auth-token") String token){
        User u = tokenProvider.getUser(token);
        Member m = memberService.findByIdAndEmail(u.getId(),u.getEmail()).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND_USER));
        return ResponseEntity.ok(MemberDto.from(m));
    }

    @PostMapping("/info")
    public ResponseEntity<MemberDto> updateInfo(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody MemberDto form, @RequestParam(name = "skill",required = false) List<String> skillList) {

        return ResponseEntity.ok(MemberDto.from(memberService.updateMember(tokenProvider.getUser(token).getId(),form, skillList)));

    }

    @PutMapping("/password/change")
    public ResponseEntity<Boolean> updateMember(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody InfoChangePasswordDto form) {
        User u = tokenProvider.getUser(token);
        form.setId(u.getId());
        return ResponseEntity.ok(this.memberService.changePassword(form));
    }

    @DeleteMapping("/info")
    public ResponseEntity<Void> deleteSkill(@RequestHeader(name = "auth-token") String token,
                                            @RequestParam Long id) {
        memberService.deleteSkill(tokenProvider.getUser(token).getId(), id);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/profile-img")
    public ResponseEntity<MemberDto> uploadProfileImg(@RequestHeader(name = "auth-token") String token,
                                                      @RequestPart MultipartFile file){

        return ResponseEntity.ok(MemberDto.from(memberService.uploadProfileImg(tokenProvider.getUser(token).getId(),file)));
    }


    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> deleteMember(@RequestHeader(name = "auth-token", required = false) String token) {
        memberService.deleteMember(tokenProvider.getUser(token).getId());

        return ResponseEntity.ok().build();
    }


    @GetMapping("/signout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

}
