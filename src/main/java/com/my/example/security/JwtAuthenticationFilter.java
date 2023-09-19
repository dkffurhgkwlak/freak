package com.my.example.security;

import com.my.example.exception.JwtAuthenticationException;
import com.my.example.service.AdminUserRoleService;
import com.my.example.web.dto.AdminUserRoleDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, java.io.IOException, AuthenticationException {
        log.info("=======AuthenticationFilter doFilterInternal=======");

        String token = jwtTokenProvider.resolveToken(request);
        //토큰확인
        if(token != null && jwtTokenProvider.validateToken(token)){

            //authToken & uri 체크 : authToken은 로그인 과정에서만 사용, 로그인 성공 후에는 loginToken 사용
            Claims claims = jwtTokenProvider.getClaims(token);
            if(claims.getAudience().equals(JwtTokenProvider.JWT_AUDIENCE_AUTH_TOKEN)
                    && !request.getRequestURI().startsWith(JwtTokenProvider.JWT_AUDIENCE_AUTH_URI)){
                throw new JwtAuthenticationException("Invalid JWT token");
            }

            //사용자 인증
            String username = claims.getSubject();
            SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(username));
        }

        filterChain.doFilter(request, response);
    }

}


