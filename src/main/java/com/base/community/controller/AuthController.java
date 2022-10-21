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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
                                                @RequestBody MemberDto form, @RequestParam(name = "skill",required = false) List<String> skillList) {

        return ResponseEntity.ok(MemberDto.from(memberService.updateMember(tokenProvider.getUser(token).getId(),form, skillList)));

    }

    @PutMapping("/changePassword")
    public ResponseEntity<Boolean> updateMember(@RequestHeader(name = "auth-token") String token,
                                                @RequestBody InfoChangePasswordDto form) {
        User u = tokenProvider.getUser(token);
        form.setId(u.getId());
        return ResponseEntity.ok(this.memberService.changePassword(form));
    }

    @DeleteMapping("/Info")
    public ResponseEntity<Void> deleteSkill(@RequestHeader(name = "auth-token") String token,
                                            @RequestParam Long id) {
        memberService.deleteSkill(tokenProvider.getUser(token).getId(), id);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/profile-img")
    public ResponseEntity<MemberDto> uploadProfileImg(@RequestHeader(name = "auth-token") String token,
                                                      @RequestPart MultipartFile file){
        MemberDto form = new MemberDto();

        String saveFilename = "";
        String urlFilename = "";
        if (file != null) {

            String originalFilename = file.getOriginalFilename();
            String baseLocalPath = "D:\\dev\\Projec\\BaseCommunity-BE\\files";
            String baseUrlPath = "/files";
            String[] arrFilename = getNewSaveFile(baseLocalPath, baseUrlPath, originalFilename);

            saveFilename = arrFilename[0];
            urlFilename = arrFilename[1];

            try {
                File newFile = new File(saveFilename);
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
            } catch (IOException e) {
                log.info("##################################################");
                log.info(e.getMessage());
            }
        }
        form.setFilename(saveFilename);
        form.setUrlFilename(urlFilename);


        return ResponseEntity.ok(MemberDto.from(memberService.uploadProfileImg(tokenProvider.getUser(token).getId(),form)));
    }


    private String[] getNewSaveFile(String baseLocalPath, String baseUrlPath, String originalFilename) {

        LocalDate now = LocalDate.now();

        String[] dirs = {
                String.format("%s/%d/", baseLocalPath, now.getYear()),
                String.format("%s/%d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue()),
                String.format("%s/%d/%02d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())};

        String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.isDirectory()) {
                file.mkdir();
            }
        }

        String fileExtension = "";
        if (originalFilename != null) {
            int dotPos = originalFilename.lastIndexOf(".");
            if (dotPos > -1) {
                fileExtension = originalFilename.substring(dotPos + 1);
            }
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newFilename = String.format("%s%s", dirs[2], uuid);
        String newUrlFilename = String.format("%s%s", urlDir, uuid);
        if (fileExtension.length() > 0) {
            newFilename += "." + fileExtension;
            newUrlFilename += "." + fileExtension;
        }

        return new String[]{newFilename, newUrlFilename};
    }

}
