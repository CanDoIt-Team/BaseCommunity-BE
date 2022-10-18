package com.base.community.component;

import com.base.community.dto.SendMailDto;
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

    public boolean sendEmail(SendMailDto mailDto) {

        boolean result = false;

        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper
                        = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(mailDto.getTo());
                mimeMessageHelper.setSubject(mailDto.getSubject());
                mimeMessageHelper.setText(mailDto.getText(), true);
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
}
