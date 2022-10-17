package com.base.community.component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class MailComponent {

    private final JavaMailSender javaMailSender;

    public boolean sendEmail(String email, String uuid) {

        boolean result = false;
        String text = getMessage(uuid);

        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper
                        = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject("BaseCommunity 인증 메일 입니다.");
                mimeMessageHelper.setText(text, true);
            }
        };

        try {
            javaMailSender.send(msg);
            result = true;
        } catch (MailException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    private String getMessage(String uuid) {
        StringBuilder text = new StringBuilder();
        text.append("<p>BaseCommunity 사이트 가입을 축하드립니다.<p>")
                .append("<p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>")
                .append("<div>")
                .append("<a target='_blank' href='http://localhost:8080/users/signup/email-auth?id=" + uuid + "'> 가입 완료 </a>")
                .append("</div>");

        return text.toString();
    }
}
