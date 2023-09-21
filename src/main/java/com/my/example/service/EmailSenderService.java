package com.my.example.service;

import com.my.example.domain.EmailContents;
import com.my.example.domain.repo.EmailContentsRepository;
import com.my.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSenderService {

    private final EmailContentsRepository emailContentsRepository;
    private final JavaMailSender javaMailSender;
    private static final String fromEmail = "dnluvletter@gmail.com";

    public EmailContents findByName(String name) {
        Optional<EmailContents> emailContents =
                Optional.ofNullable(emailContentsRepository.findByName(name)
                        .orElseThrow(() -> new NotFoundException("email name not found")));

        return emailContents.get();
    }
    public void sendEmail(String toAddress, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        sendEmail(fromEmail, toAddress, subject, content);
    }
    public void sendEmail(String fromAddress, String toAddress, String subject, String content) throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

//_subject.replace("${OTP}", otp)
        mimeMessageHelper.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setFrom(new InternetAddress(fromAddress, fromAddress, "UTF-8"));
        mimeMessageHelper.setTo(new InternetAddress[] {new InternetAddress(toAddress)});

        javaMailSender.send(mimeMessage);
    }
}
