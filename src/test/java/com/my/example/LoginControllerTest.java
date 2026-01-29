package com.my.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.example.domain.AccountCredentials;
import com.my.example.domain.AdminUser;
import com.my.example.security.JwtTokenProvider;
import com.my.example.service.AdminUserRoleService;
import com.my.example.service.AuthOTPService;
import com.my.example.service.UserDetailsServiceImpl;
import com.my.example.web.controller.LoginConroller;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginConroller.class)
@DisplayName("로그인 컨트롤러 단위 테스트")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AdminUserRoleService adminRoleService;

    @MockBean
    private AuthOTPService authOTPService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("/auth 엔드포인트")
    class AuthEndpointTest {

        @Test
        @DisplayName("유효한 인증 정보로 토큰 발급 성공")
        void authSuccessWithValidCredentialsTest() throws Exception {
            // given
            AccountCredentials credentials = AccountCredentials.builder()
                    .username("testuser")
                    .password("password")
                    .build();

            AdminUser mockUser = AdminUser.builder()
                    .id(1L)
                    .uid("testuser")
                    .userName("Test User")
                    .userEmail("test@example.com")
                    .userStatus("ACTIVE")
                    .passwordFailCnt(0)
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();

            Authentication mockAuth = new UsernamePasswordAuthenticationToken(
                    mockUser, null, mockUser.getAuthorities());

            String authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

            when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
            when(jwtTokenProvider.generateToken(
                    eq("testuser"),
                    eq(JwtTokenProvider.JWT_AUDIENCE_AUTH_TOKEN),
                    anyList())).thenReturn(authToken);

            String jsonBody = objectMapper.writeValueAsString(credentials);

            // when & then
            MvcResult result = mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                    .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + authToken))
                    .andReturn();
        }

        @Test
        @DisplayName("잘못된 JSON 형식 - 400 Bad Request")
        void authFailWithInvalidJsonTest() throws Exception {
            // given
            String invalidJson = "{ invalid json }";

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("NULL username - 검증 실패")
        void authFailWithNullUsernameTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username(null)
                            .password("password")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("NULL password - 검증 실패")
        void authFailWithNullPasswordTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password(null)
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Content-Type 없음 - 400 Bad Request")
        void authFailWithoutContentTypeTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("password")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("/auth/otp 엔드포인트")
    class AuthOTPEndpointTest {

        @Test
        @WithMockUser(username = "testuser", roles = {"ADMIN"})
        @DisplayName("OTP 발송 요청 - 인증된 사용자")
        void sendOTPWithAuthenticatedUserTest() throws Exception {
            // given
            AdminUser mockUser = AdminUser.builder()
                    .id(1L)
                    .uid("testuser")
                    .userName("Test User")
                    .userEmail("test@example.com")
                    .userStatus("ACTIVE")
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();

            when(authOTPService.createOTP()).thenReturn("123456");

            // when & then
            mockMvc.perform(post("/auth/otp")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("OTP 발송 요청 - 미인증 사용자 (401 Unauthorized)")
        void sendOTPWithoutAuthenticationTest() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/otp")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("에러 응답")
    class ErrorResponseTest {

        @Test
        @DisplayName("요청 본문 없음 - 400 Bad Request")
        void requestWithoutBodyTest() throws Exception {
            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("지원하지 않는 Content-Type - 415 Unsupported Media Type")
        void requestWithUnsupportedContentTypeTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("password")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }
}
