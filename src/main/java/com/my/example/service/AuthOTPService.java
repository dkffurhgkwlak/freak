package com.my.example.service;

import com.my.example.domain.EmailContents;
import com.my.example.domain.SessionSave;
import com.my.example.domain.repo.SessionSaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthOTPService {
    private final SessionSaveRepository sessionSaveRepository;
    private final EmailSenderService emailSenderService;

    public String createOTP(){
        return RandomStringUtils.randomNumeric(4);
    }
    public void sendOTP(String email, String otp) throws MessagingException, UnsupportedEncodingException {
        EmailContents emailContents = emailSenderService.findByName("AUTH_OTP");
        emailContents.setSubject(emailContents.getSubject().replace("{otp}",otp));
        emailContents.setContent(emailContents.getContent().replace("{otp}",otp));
        emailSenderService.sendEmail(email,emailContents.getSubject(),emailContents.getContent());
    }

    public void saveOTP(SessionSave sessionSave){
        sessionSaveRepository.save(sessionSave);
    }

    public boolean validateOTP(String sessionKey, String otp){
        SessionSave data = sessionSaveRepository.findBySessionKey(sessionKey);
        if(data!= null && data.getSessionValue().equals(otp)){
            sessionSaveRepository.deleteById(data.getId());
            return true;
        }
        return false;
    }


}
