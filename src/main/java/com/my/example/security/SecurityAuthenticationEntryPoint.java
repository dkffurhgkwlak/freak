package com.my.example.security;

import com.my.example.common.CommonExceptionResponse;
import com.my.example.web.dto.BaseExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
@Slf4j
@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException ex) throws IOException {
        log.debug("=======CustomAuthenticationEntryPoint=======");
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String resStr = objectMapper.writeValueAsString(
                BaseExceptionDto.builder().
                        errorCode(CommonExceptionResponse.SECURITY_AUTHENTICATION.getCode()).
                        errorMessage(CommonExceptionResponse.SECURITY_AUTHENTICATION.getMessage()).
                        build()
        );

        PrintWriter writer = response.getWriter();
        writer.println(resStr);
    }
}