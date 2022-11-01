package com.base.community.service;

import com.base.community.component.MailComponent;
import com.base.community.dto.*;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.entity.MemberSkills;
import com.base.community.model.repository.MemberRepository;
import com.base.community.model.repository.MemberSkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.base.community.exception.ErrorCode.*;
import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {

    private final MailComponent mailComponent;
    private final MemberRepository memberRepository;
    private final MemberSkillsRepository memberSkillsRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${dir.LocalPath}")
    private String baseLocalPath;

    @Value("${dir.UrlPath}")
    private String baseUrlPath;


    //회원가입
    @Transactional
    public Member signup(SignUpDto signUpDto) {
        if (this.memberRepository.existsByEmail(signUpDto.getEmail())) {
            throw new CustomException(ALREADY_REGISTERED_USER);
        }

        if (this.memberRepository.existsByNickname(signUpDto.getNickname())) {
            throw new CustomException(ALREADY_REGISTERED_NICKNAME);
        }

        String uuid = UUID.randomUUID().toString();
        String encPassword = BCrypt.hashpw(signUpDto.getPassword(), BCrypt.gensalt());
        signUpDto.setPassword(encPassword);

        Member member = memberRepository.save(Member.from(signUpDto));
        member.setEmailAuthKey(uuid);

        SendMailDto mailDto = SendMailDto.builder()
                .to(signUpDto.getEmail())
                .subject("BaseCommunity 인증 메일 입니다.")
                .text("<p>BaseCommunity 사이트 가입을 축하드립니다.<p>"
                        + "<p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
                        + "<a target='_blank' href='http://localhost:3000/users/email-auth?id="
                        + uuid + "'> 가입 완료 </a></div>")
                .build();
        log.info(signUpDto.getEmail() + "회원 인증 이메일 발송완료");
        mailComponent.sendEmail(mailDto);

        return member;
    }


    //회원가입 - 닉네임 체크
    public boolean checkNickName(String nickname) {
        return this.memberRepository.existsByNickname(nickname);
    }

    //회원가입 - 이메일 체크
    public boolean checkEmail(String email) {
        return this.memberRepository.existsByEmail(email);
    }

    //회원가입 - 이메일 인증
    public boolean emailAuth(String uuid) {
        Optional<Member> optionalMember = this.memberRepository.findByEmailAuthKey(uuid);
        if (optionalMember.isEmpty()) {
            return false;
        }

        Member member = optionalMember.get();
        if (member.isEmailAuth()) {
            return false;
        }

        member.setEmailAuth(true);
        member.setEmailAuthDate(LocalDateTime.now());
        member.setUserStatus(MEMBER_STATUS_ING.getStatus());
        memberRepository.save(member);

        return true;
    }

    //로그인 페이지(비밀번호 재설정-유저 정보 입력)
    public boolean findPassword(ChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository
                .findByEmailAndName(form.getEmail(), form.getName());
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();
        member.setChangePasswordKey(uuid);
        member.setChangePasswordLimitDt(LocalDateTime.now().plusHours(24)); // 24 시간 동안 유효
        memberRepository.save(member);

        SendMailDto mailDto = SendMailDto.builder()
                .to(form.getEmail())
                .subject("BaseCommunity 비밀번호 재설정 메일입니다.")
                .text("아래 링크를 클릭하셔서 비밀번호를 변경 해주세요."
                        + "<div><a target='_blank' href='http://localhost:3000/password/new?uuid="
                        + uuid + "'> 비밀번호 재설정 링크 </a></div>")
                .build();
        log.info(form.getEmail() + "회원 비밀번호 변경 이메일 발송완료");
        mailComponent.sendEmail(mailDto);

        return true;
    }

    //로그인 페이지(비밀번호 재설정-새로운 비밀번호 입력)
    public String changePassword(String uuid, ChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository.findByChangePasswordKey(uuid);

        if (!optionalMember.isPresent()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        if (member.getChangePasswordLimitDt() == null) {
            throw new CustomException(NOT_VALID_DATE);
        }
        if (member.getChangePasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new CustomException(NOT_VALID_DATE);
        }

        String encPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setChangePasswordKey("");
        member.setChangePasswordLimitDt(null);
        memberRepository.save(member);

        return "비밀번호 변경이 완료됐습니다.";
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) this.memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username));
    }


    //로그인
    public Member authenticate(SignInDto form) {
        Optional<Member> optionalMember = memberRepository.findByEmail(form.getEmail());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        if (!this.passwordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }
        if (member.getUserStatus().equals("REQ")) {
            throw new CustomException(NOT_AUTHENTICATE_USER);
        }
        if (member.getUserStatus().equals("WITHDRAW")) {
            throw new CustomException(WITHDRAW_USER);
        }
//        var user = this.memberRepository.findByEmail(form.getEmail())
//                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));
//
//        if (!this.passwordEncoder.matches(form.getPassword(), user.getPassword())) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
//        }
        return member;
    }


    //마이페이지 - 회원정보 가져오기
    public Member getMemberDetail(User user) {
        return memberRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }


    //마이페이지 -비밀번호 재설정
    public String changeInfoPassword(Long id, InfoChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        String encPassword = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return "비밀번호 변경이 완료됐습니다.";
    }


    // 멤버정보 업데이트
    public Member updateMember(Long id, UpdateMemberDto form, String skill) {
        form.setId(id);
        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();


        member.setNickname(form.getNickname());
        member.setPhone(form.getPhone());
        member.setBirth(form.getBirth());


        if (!skill.isEmpty()) {
            skill = skill.replaceAll("value","\"value\"");
            HashSet<String> skillList = new HashSet<>();
            try {
                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(skill);
                json.forEach(item -> {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(item.toString());
                    skillList.add(jsonObject.get("value").toString());
                });

//                기존 해쉬태그 비교
                HashSet<MemberSkills> OriginSkills = memberSkillsRepository.findAllByMemberId(form.getId());
                HashSet<String> OriginSkillsName = new HashSet<>();
                OriginSkills.forEach(item -> {
                    OriginSkillsName.add(item.getName());
                });


//                추가된 해쉬태그
                HashSet<String> addSkills = new HashSet<>(skillList);
                addSkills.removeAll(OriginSkillsName);
                if (!addSkills.isEmpty()) {
                    List<MemberSkillsDto.Request> memberSkillDtoList = new ArrayList<>();
                    addSkills.forEach(item -> {
                        MemberSkillsDto.Request memberSkillDto = new MemberSkillsDto.Request();
                        memberSkillDto.setName(item);
                        memberSkillDtoList.add(memberSkillDto);
                    });
                    SaveAll(form.getId(), memberSkillDtoList);
                }

//                삭제된 해시태그
                HashSet<String> SubSkills = new HashSet<>(OriginSkillsName);
                SubSkills.removeAll(skillList);
                List<String> setToList = new ArrayList<>(SubSkills);

                if (!SubSkills.isEmpty()) {
                    DeleteAll(form.getId(), setToList);
                }
            } catch (ParseException e) {
                log.info(e.getMessage());
            }
        }
        memberRepository.save(member);
        return member;
    }

    private void DeleteAll(Long id, List<String> skill) {
        memberSkillsRepository.deleteByMemberIdAndNameIn(id, skill);
    }


    private void SaveAll(Long id, List<MemberSkillsDto.Request> memberSkillDtoList) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        List<MemberSkills> memberSkills = new ArrayList<>();
        for (int i = 0; i < memberSkillDtoList.size(); i++) {
            memberSkillDtoList.get(i).setMember(member);
            memberSkills.add(memberSkillDtoList.get(i).toEntity());
        }
        memberSkillsRepository.saveAll(memberSkills);
    }


    //프로필 업데이트
    public Member uploadProfileImg(Long id, MultipartFile file) {

        MemberDto form = new MemberDto();

        String saveFilename = "";
        String urlFilename = "";

        if (file != null) {

            String originalFilename = file.getOriginalFilename();
            String[] arrFilename = getNewSaveFile(baseLocalPath, baseUrlPath, originalFilename);

            saveFilename = arrFilename[0];
            urlFilename = arrFilename[1];

            try {
                File newFile = new File(saveFilename);
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }

        form.setFilename(saveFilename);
        form.setUrlFilename(urlFilename);
        form.setId(id);

        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        member.setFilename(form.getFilename());
        member.setUrlFilename(form.getUrlFilename());
        memberRepository.save(member);
        return member;

    }


    //회원탈퇴
    public String deleteMember(Long id) {
        MemberDto form = new MemberDto();
        form.setId(id);

        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        memberRepository.delete(member);

        return "회원 탈퇴가 완료되었습니다.";
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
