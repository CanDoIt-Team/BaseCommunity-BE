package com.base.community.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
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
    private final AmazonS3 amazonS3;
    private final ModelMapper modelMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    //????????????
    @Transactional
    public MemberDto signup(SignUpDto signUpDto) {
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
                .subject("BaseCommunity ?????? ?????? ?????????.")
                .text("<p>BaseCommunity ????????? ????????? ??????????????????.<p>"
                        + "<p>?????? ????????? ??????????????? ????????? ?????? ?????????.</p>"
                        + "<a target='_blank' href='https://basecommunity.netlify.app/users/email-auth?id="
                        + uuid + "'> ?????? ?????? </a></div>")
                .build();
        log.info("# " + signUpDto.getEmail() + "?????? ?????? ????????? ????????????");
        mailComponent.sendEmail(mailDto);

        MemberDto memberDto = modelMapper.map(member, MemberDto.class);

        return memberDto;
    }


    //???????????? - ????????? ??????
    public boolean checkNickName(String nickname) {
        return this.memberRepository.existsByNickname(nickname);
    }

    //???????????? - ????????? ??????
    public boolean checkEmail(String email) {
        return this.memberRepository.existsByEmail(email);
    }

    //???????????? - ????????? ??????
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

    //????????? ?????????(???????????? ?????????-?????? ?????? ??????)
    public boolean findPassword(ChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository
                .findByEmailAndName(form.getEmail(), form.getName());
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();
        member.setChangePasswordKey(uuid);
        member.setChangePasswordLimitDt(LocalDateTime.now().plusHours(24)); // 24 ?????? ?????? ??????
        memberRepository.save(member);

        SendMailDto mailDto = SendMailDto.builder()
                .to(form.getEmail())
                .subject("BaseCommunity ???????????? ????????? ???????????????.")
                .text("?????? ????????? ??????????????? ??????????????? ?????? ????????????."
                        + "<div><a target='_blank' href='https://basecommunity.netlify.app/password/new?uuid="
                        + uuid + "'> ???????????? ????????? ?????? </a></div>")
                .build();
        log.info(form.getEmail() + "?????? ???????????? ?????? ????????? ????????????");
        mailComponent.sendEmail(mailDto);

        return true;
    }

    //????????? ?????????(???????????? ?????????-????????? ???????????? ??????)
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

        return "???????????? ????????? ??????????????????.";
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) this.memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("???????????? ?????? ??? ????????????." + username));
    }


    //?????????
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
//                .orElseThrow(() -> new RuntimeException("???????????? ?????? ID ?????????."));
//
//        if (!this.passwordEncoder.matches(form.getPassword(), user.getPassword())) {
//            throw new RuntimeException("??????????????? ???????????? ????????????.");
//        }

        return member;
    }


    //??????????????? - ???????????? ????????????
    public MemberDto getMemberDetail(User user) {
        Member member = memberRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        return modelMapper.map(member, MemberDto.class);
    }


    //??????????????? -???????????? ?????????
    public String changeInfoPassword(Long id, InfoChangePasswordDto form) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (!optionalMember.isPresent()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();
        String encPassword = BCrypt.hashpw(form.getNewPassword(), BCrypt.gensalt());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return "???????????? ????????? ??????????????????.";
    }


    // ???????????? ????????????
    public MemberDto updateMember(Long id, UpdateMemberDto form, String skill) {
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
            HashSet<String> skillList = new HashSet<>();
            try {
                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(skill);
                json.forEach(item -> {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(item.toString());
                    skillList.add(jsonObject.get("value").toString());
                });

//                ?????? ???????????? ??????
                HashSet<MemberSkills> OriginSkills = memberSkillsRepository.findAllByMemberId(form.getId());
                HashSet<String> OriginSkillsName = new HashSet<>();
                OriginSkills.forEach(item -> {
                    OriginSkillsName.add(item.getName());
                });


//                ????????? ????????????
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

//                ????????? ????????????
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
        return modelMapper.map(member, MemberDto.class);
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

    @Transactional
    public MemberDto uploadProfileImg(Long memberId, MultipartFile file) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // ?????? ????????? ??????
        if (ObjectUtils.isNotEmpty(member.getFilename())) {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, member.getFilename()));
        }

        // ?????? ????????? ??????
        String fileName = UUID.randomUUID().toString().concat(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "????????? ???????????? ?????????????????????.");
        }

        member.setFilename(fileName);
        member.setUrlFilename(amazonS3.getUrl(bucket, fileName).toString());

        return modelMapper.map(member, MemberDto.class);
    }


    //????????????
    public String deleteMember(Long id) {
        MemberDto form = new MemberDto();
        form.setId(id);

        Optional<Member> optionalMember = memberRepository.findById(form.getId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(NOT_FOUND_USER);
        }
        Member member = optionalMember.get();

        memberRepository.delete(member);

        return "?????? ????????? ?????????????????????.";
    }
}
