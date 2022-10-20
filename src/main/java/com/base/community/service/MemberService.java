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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.base.community.exception.ErrorCode.*;
import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MailComponent mailComponent;
    private final MemberRepository memberRepository;
    private final MemberSkillsRepository memberSkillsRepository;

    private final PasswordEncoder passwordEncoder;

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
                        + "<a target='_blank' href='http://localhost:8080/users/signup/email-auth?id="
                        + uuid + "'> 가입 완료 </a></div>")
                .build();
        mailComponent.sendEmail(mailDto);

        return member;
    }

    public boolean checkNickName(String nickname) {
        return this.memberRepository.existsByNickname(nickname);
    }

    public boolean checkEmail(String email) {
        return this.memberRepository.existsByEmail(email);
    }

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
                        + "<div><a target='_blank' href='http://localhost:8080/users/changepassword?uuid="
                        + uuid + "'> 비밀번호 재설정 링크 </a></div>")
                .build();

        mailComponent.sendEmail(mailDto);

        return true;
    }
    //로그인 페이지(비밀번호 재설정-새로운 비밀번호 입력)
    public boolean changePassword(String uuid, String password) {
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

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setChangePasswordKey("");
        member.setChangePasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) this.memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + username));
    }


    //로그인
    public Member authenticate(SignInDto member) {

        var user = this.memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    public Optional<Member> findByIdAndEmail(Long id, String email) {
        return memberRepository.findById(id)
                .stream().filter(member -> member.getEmail().equals(email))
                .findFirst();
    }


    //마이페이지 (비밀번호 재설정)
    public boolean changePassword(InfoChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (!optionalMember.isPresent()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        String encPassword = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return true;
    }


    public Member updateMember(Long id, UpdateMemberDto form) {
        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        member.setPhone(form.getPhone());
        member.setBirth(form.getBirth());
        memberRepository.save(member);

        return member;
    }


    //멤버정보 업데이트
    public Member updateMember(Long id, MemberDto form, List<String> skillList) {
        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        member.setPhone(form.getPhone());
        member.setBirth(form.getBirth());

        MemberSkills memberSkills = MemberSkills.of(skillList);
        member.getSkills().add(memberSkills);

        memberRepository.save(member);
        return member;
    }


    public void deleteSkill(Long memberId, Long skillId) {
        MemberSkills memberSkills = memberSkillsRepository.findByMemberIdAndId(memberId, skillId)
                .orElseThrow(()->new CustomException(NOT_FOUND_SKILL));

        memberSkillsRepository.delete(memberSkills);
    }
}
