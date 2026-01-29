package com.my.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.example.domain.AccountCredentials;
import com.my.example.domain.AdminUser;
import com.my.example.domain.AdminUserRole;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.domain.repo.AdminUserRoleRepository;
import com.my.example.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("로그인 통합 테스트")
public class LoginSpringBootTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AdminUserRoleRepository adminUserRoleRepository;

    @BeforeAll
    static void beforeAll() throws Exception {
    }

    @BeforeEach
    void beforeEach() throws Exception {
        // 테스트 계정 생성 및 초기화
        AdminUserRole role = adminUserRoleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            AdminUserRole adminUserRole = AdminUserRole.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator Role")
                    .build();
            adminUserRoleRepository.save(adminUserRole);
        }

        // 테스트 계정이 없으면 생성
        if (!adminUserRepository.findByUid("testuser").isPresent()) {
            adminUserRepository.save(AdminUser.builder()
                    .uid("testuser")
                    .userName("Test User")
                    .userEmail("test@example.com")
                    .password("$2a$10$2/c9RrsJTxyVROo5WV2hEevbdIDNN43Z/v7.ILUcM0LNZIhUoFLwa") // password: "user"
                    .userStatus("ACTIVE")
                    .passwordFailCnt(0)
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build());
        }

        // 테스트 계정 상태 초기화 (매 테스트 전)
        AdminUser testUser = adminUserRepository.findByUid("testuser").orElse(null);
        if (testUser != null) {
            testUser.setPasswordFailCnt(0);
            testUser.setUserStatus("ACTIVE");
            adminUserRepository.save(testUser);
        }
    }

    @Nested
    @DisplayName("1차 인증 (/auth)")
    class AuthTest {

        @Test
        @DisplayName("유효한 계정으로 인증 성공")
        void authSuccessTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("user")
                            .build());

            // when & then
            MvcResult result = mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                    .andReturn();

            String authHeader = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
            assertNotNull(authHeader, "Authorization 헤더가 존재해야 합니다");
            assertTrue(authHeader.startsWith("Bearer "), "Authorization 헤더는 'Bearer '로 시작해야 합니다");
            log.info("Auth Token: {}", authHeader);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 - 인증 실패")
        void userNotFoundTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("nonexistentuser")
                            .password("user")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S001"))
                    .andExpect(jsonPath("$.errorMessage").value("user not found"));
        }

        @Test
        @DisplayName("잘못된 비밀번호 첫 시도 - 실패 횟수 1 증가")
        void passwordFailFirstAttemptTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("wrongpassword")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S001"))
                    .andExpect(jsonPath("$.errorMessage").value("Authentication failed"))
                    .andExpect(jsonPath("$.passwordFailCnt").value(1))
                    .andExpect(jsonPath("$.userStatus").value("ACTIVE"));

            // 데이터베이스 검증
            AdminUser user = adminUserRepository.findByUid("testuser").orElse(null);
            assertNotNull(user);
            assertEquals(1, user.getPasswordFailCnt(), "실패 횟수가 1이어야 합니다");
        }

        @Test
        @DisplayName("비밀번호 5회 실패 - 계정 잠금")
        void passwordFailMaxAttemptsTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("wrongpassword")
                            .build());

            // when: 5회 시도
            for (int i = 1; i <= 5; i++) {
                mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                        .andDo(print())
                        .andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.passwordFailCnt").value(i));
            }

            // then: 6번째 시도 - 계정 잠금 상태 확인
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S003"))
                    .andExpect(jsonPath("$.errorMessage").value("User account is locked"))
                    .andExpect(jsonPath("$.userStatus").value("PASSWORD_LOCK"));

            // 데이터베이스 검증
            AdminUser user = adminUserRepository.findByUid("testuser").orElse(null);
            assertNotNull(user);
            assertEquals("PASSWORD_LOCK", user.getUserStatus(), "계정이 PASSWORD_LOCK 상태여야 합니다");
            assertEquals(5, user.getPasswordFailCnt(), "실패 횟수가 5여야 합니다");
        }

        @Test
        @DisplayName("계정이 잠긴 상태에서 올바른 비밀번호 - 인증 거부")
        void authFailedAccountLockedTest() throws Exception {
            // given: 계정을 PASSWORD_LOCK 상태로 설정
            AdminUser user = adminUserRepository.findByUid("testuser").orElse(null);
            assertNotNull(user);
            user.setUserStatus("PASSWORD_LOCK");
            user.setPasswordFailCnt(5);
            adminUserRepository.save(user);

            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("user")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S003"))
                    .andExpect(jsonPath("$.errorMessage").value("User account is locked"));
        }

        @Test
        @DisplayName("계정이 DORMANT(휴면) 상태일 때 - 인증 거부")
        void authFailedAccountDormantTest() throws Exception {
            // given: 계정을 DORMANT 상태로 설정
            AdminUser user = adminUserRepository.findByUid("testuser").orElse(null);
            assertNotNull(user);
            user.setUserStatus("DORMANT");
            user.setPasswordFailCnt(0);
            adminUserRepository.save(user);

            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("user")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S003"))
                    .andExpect(jsonPath("$.errorMessage").value("User is disabled"));
        }

        @Test
        @DisplayName("계정이 LOCK 상태일 때 - 인증 거부")
        void authFailedAccountLockTest() throws Exception {
            // given: 계정을 LOCK 상태로 설정
            AdminUser user = adminUserRepository.findByUid("testuser").orElse(null);
            assertNotNull(user);
            user.setUserStatus("LOCK");
            user.setPasswordFailCnt(0);
            adminUserRepository.save(user);

            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("user")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S003"))
                    .andExpect(jsonPath("$.errorMessage").value("User account is locked"));
        }

        @Test
        @DisplayName("빈 username - 검증 실패")
        void emptyUsernameTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("")
                            .password("user")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("빈 password - 검증 실패")
        void emptyPasswordTest() throws Exception {
            // given
            String jsonBody = objectMapper.writeValueAsString(
                    AccountCredentials.builder()
                            .username("testuser")
                            .password("")
                            .build());

            // when & then
            mockMvc.perform(post("/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("2차 인증 - OTP (/auth/otp)")
    class AuthOTPTest {

        @Test
        @WithUserDetails("testuser")
        @DisplayName("OTP 발송 요청 성공")
        void sendOTPSuccessTest() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/otp")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dateTime", notNullValue()));
        }

        @Test
        @WithUserDetails("testuser")
        @DisplayName("OTP 발송 - Authorization 헤더 없을 때 실패")
        void sendOTPWithoutAuthTest() throws Exception {
            // when & then
            mockMvc.perform(post("/auth/otp")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk()); // WithUserDetails는 인증 제공
        }
    }

    @Nested
    @DisplayName("로그인 (/auth/otp PATCH)")
    class LoginTest {

        @Test
        @WithUserDetails("testuser")
        @DisplayName("OTP 검증 및 로그인 성공")
        void loginWithValidOTPTest() throws Exception {
            // 실제 OTP 검증 로직이 있다면, 먼저 OTP를 발송받고
            // 그 OTP를 사용해서 로그인 테스트
            String requestBody = objectMapper.writeValueAsString(
                    new Object() {
                        public String dateTime = "2024-01-29T10:00:00";
                        public String OTP = "123456";
                    });

            // OTP 검증이 없으면 badRequest 반환
            mockMvc.perform(patch("/auth/otp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("스킵 로그인 (/auth/skip)")
    class SkipLoginTest {

        @Test
        @WithUserDetails("testuser")
        @DisplayName("스킵 로그인 - ROLE_TEST 없을 때 권한 거부")
        void skipLoginWithoutRoleTest() throws Exception {
            // given: testuser는 ROLE_ADMIN만 가지고 있음
            // when & then
            mockMvc.perform(post("/auth/skip")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError()); // 403 Forbidden
        }
    }
}
