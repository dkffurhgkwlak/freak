package com.my.example.security;

import com.my.example.common.CommonExceptionResponse;
import com.my.example.domain.AdminUser;
import com.my.example.exception.ApiException;
import com.my.example.service.UserDetailsServiceImpl;
import com.my.example.web.dto.AdminUserDto;
import com.my.example.web.dto.LoginDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AdminUserAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsServiceImpl userDetailsService;
    private PasswordEncoder passwordEncoder;

    public AdminUserAuthenticationProvider(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new InternalAuthenticationServiceException("Authentication is null");
        }
        String username = authentication.getName();
        if (authentication.getCredentials() == null) {
            throw new AuthenticationCredentialsNotFoundException("Credentials is null");
        }
        String password = authentication.getCredentials().toString();
        UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        //계정잠금 확인
        if (!loadedUser.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }
        if (!loadedUser.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
        if (!loadedUser.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }
        //비밀번호 실패
        if (!passwordEncoder.matches(password, loadedUser.getPassword())) {
            AdminUserDto adminUser = userDetailsService.passwordFail(loadedUser.getUsername());
            throw new ApiException("Password does not match stored value", ResponseEntity
                    .status(CommonExceptionResponse.SECURITY_AUTHENTICATION.getStatus())
                    .body(LoginDto.builder()
                            .passwordFailCnt(adminUser.getPasswordFailCnt())
                            .userStatus(adminUser.getUserStatus())
                            .errorCode(CommonExceptionResponse.SECURITY_AUTHENTICATION.getCode())
                            .errorMessage(CommonExceptionResponse.SECURITY_AUTHENTICATION.getMessage()).build()));
        }
        if (!loadedUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials have expired");
        }
        //인증
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
