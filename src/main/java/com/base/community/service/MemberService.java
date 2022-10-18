package com.base.community.service;

import com.base.community.component.MailComponent;
import com.base.community.dto.ChangePasswordDto;
import com.base.community.dto.SignUpDto;
import com.base.community.exception.CustomException;
import com.base.community.exception.ErrorCode;
import com.base.community.model.entity.Member;
import com.base.community.model.repository.MemberRepository;
import com.base.community.type.MemberCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.base.community.exception.ErrorCode.*;
import static com.base.community.type.MemberCode.MEMBER_STATUS_ING;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MailComponent mailComponent;
    private final MemberRepository memberRepository;

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

        Member member = memberRepository.save(Member.from(signUpDto, uuid));

        mailComponent.sendEmail(signUpDto.getEmail(), uuid);

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

    public boolean findPassword(ChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository
                .findByEmailAndName(form.getEmail(), form.getName());
        if(optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();
        member.setChangePasswordKey(uuid);
        member.setChangePasswordLimitDt(LocalDateTime.now().plusHours(24)); // 24 시간 동안 유효
        memberRepository.save(member);


        String email = form.getEmail();
        String subject = "BaseCommunity 비밀번호 재설정 메일입니다.";
        String text = "아래 링크를 클릭하셔서 비밀번호를 변경 해주세요." +
                "<div><a target='_blank' href='http://localhost:8080/users/changepassword?uuid=" + uuid + "'> 비밀번호 재설정 링크 </a></div>";

        mailComponent.sendMail2(email, subject, text);

        return true;
        
        
    }

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
}
