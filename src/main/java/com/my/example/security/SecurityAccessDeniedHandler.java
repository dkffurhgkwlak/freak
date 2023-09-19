package com.my.example.security;

import com.my.example.common.CommonExceptionResponse;
import com.my.example.web.dto.BaseExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException ex) throws IOException, ServletException {
        log.debug("=======CustomAccessDeniedHandler=======");
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String resStr = objectMapper.writeValueAsString(
                BaseExceptionDto.builder().
                        errorCode(CommonExceptionResponse.ACCESS_DENIED_EXCEPTION.getCode()).
                        errorMessage(CommonExceptionResponse.ACCESS_DENIED_EXCEPTION.getMessage()).
                        build()
        );

        PrintWriter writer = response.getWriter();
        writer.println(resStr);
    }

}
