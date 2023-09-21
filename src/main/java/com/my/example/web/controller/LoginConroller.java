package com.my.example.web.controller;

import com.my.example.domain.AccountCredentials;
import com.my.example.domain.AdminUser;
import com.my.example.domain.SessionSave;
import com.my.example.security.JwtTokenProvider;
import com.my.example.service.AdminUserRoleService;
import com.my.example.service.AuthOTPService;
import com.my.example.service.UserDetailsServiceImpl;
import com.my.example.web.dto.AdminUserRoleDto;
import com.my.example.web.dto.AuthOTPDto;
import com.my.example.web.dto.LoginDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Tag(name = "로그인", description = "로그인 관련 api 입니다.")
@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginConroller {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final AdminUserRoleService adminRoleService;
    private final AuthOTPService authOTPService;

//    @Operation(summary = "계정 인증", description = "username password 확인")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation")
//    })
    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> auth(@Valid @RequestBody AccountCredentials credentials) {

        //인증
        Authentication authentication = authenticationManager.authenticate(
                jwtTokenProvider.getAuthentication(credentials)
        );

        //사용자
        AdminUser adminUser = (AdminUser)authentication.getPrincipal();

        //auth 토큰생성
        String authToken = jwtTokenProvider.generateToken(adminUser.getUsername(), JwtTokenProvider.JWT_AUDIENCE_AUTH_TOKEN, adminUser.getRoles());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .build();
    }
    //@RequestHeader(value = "Authorization") String token
//    @Operation(summary = "2차 인증 OTP 전송", description = "등록된 사용자 이메일로 OTP 전송")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthOTPDto.class)))
//    })
    @PostMapping(value = "/auth/otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendOTP(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        //사용자
        AdminUser user = (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime localDateTime = LocalDateTime.now();

        //OTP 생성
        String otp = authOTPService.createOTP();

        //OTP 이메일 전송
        authOTPService.sendOTP(user.getUserEmail(),otp);

        //sessionKey 생성
        String sessionKey = user.getUid() +
                request.getRequestURI() +
                localDateTime.toString();

        //OTP 저장 in session
        authOTPService.saveOTP(SessionSave.builder().
                sessionKey(sessionKey).
                sessionValue(otp).
                expireTime(LocalDateTime.now().plusMinutes(5)).build());

        return ResponseEntity.ok().body(AuthOTPDto.builder().dateTime(localDateTime.toString()).build());
    }

//    @Operation(summary = "2차 인증 OTP 확인", description = "OTP 검증, 로그인 성공")
//    @ApiResponses(value = {
//          @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginDto.class)))
//    })
    @Transactional
    @PatchMapping(value = "/auth/otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody AuthOTPDto requestParams, HttpServletRequest request) {

        //사용자
        AdminUser adminUser = (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //sessionKey 생성
        String sessionKey = adminUser.getUid() +
                request.getRequestURI() +
                requestParams.getDateTime();

        //OTP 검증 in session
        if(!authOTPService.validateOTP(sessionKey, requestParams.getOTP())){
                return ResponseEntity.badRequest().build();
        }

        //로그인
        userDetailsService.login(adminUser.getUsername());

        //login 토큰생성
        String loginToken = jwtTokenProvider.generateToken(adminUser.getUsername(), JwtTokenProvider.JWT_AUDIENCE_LOGIN_TOKEN, adminUser.getRoles());

        // redis에 저장
//        redisTemplate.opsForValue().set(
//                userUid,
//                loginToken,
//                tokenFirstMillisecond,
//                TimeUnit.MILLISECONDS
//        );

        AdminUserRoleDto adminUserRole = adminRoleService.getAdminUserRoleAndPages(adminUser.getRoles().get(0));

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(LoginDto.builder().userName(adminUser.getUsername())
                        .role(adminUserRole.getName())
                        .pages(adminUserRole.getPages()).build());

    }

    @Transactional
    @Secured("ROLE_TEST")
    @PostMapping(value = "/auth/skip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> simpleLogin(HttpServletRequest request) {

        //사용자
        AdminUser adminUser = (AdminUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //로그인
        userDetailsService.login(adminUser.getUsername());

        //login 토큰생성
        String loginToken = jwtTokenProvider.generateToken(adminUser.getUsername(), JwtTokenProvider.JWT_AUDIENCE_LOGIN_TOKEN, adminUser.getRoles());

        // redis에 저장
//        redisTemplate.opsForValue().set(
//                userUid,
//                loginToken,
//                tokenFirstMillisecond,
//                TimeUnit.MILLISECONDS
//        );

        AdminUserRoleDto adminUserRole = adminRoleService.getAdminUserRoleAndPages(adminUser.getRoles().get(0));

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginToken)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(LoginDto.builder().userName(adminUser.getUsername())
                        .role(adminUserRole.getName())
                        .pages(adminUserRole.getPages()).build());

    }


}