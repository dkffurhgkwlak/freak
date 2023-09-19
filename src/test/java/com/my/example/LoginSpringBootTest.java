package com.my.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.example.domain.AccountCredentials;
import com.my.example.domain.AdminUser;
import com.my.example.domain.repo.AdminUserRepository;
import com.my.example.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class LoginSpringBootTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() throws Exception {
    }
    @BeforeEach
    void beforeEach() throws Exception {
    }
//    @Test
//    @Order(1)
//    @DisplayName("테스트 계정 생성")
//    public void setup() throws Exception {
//        //테스트 계정 생성
//        AdminUserRole role = adminUserRoleRepository.findByName("ROLE_TEST");
//        if (role == null) {
//            AdminUserRole adminUserRole = AdminUserRole.builder().name("ROLE_TEST").description("TEST").build();
//            adminUserRoleRepository.save(adminUserRole);
//        }
//        if(!adminUserRepository.findByUid("user").isPresent()){
//            adminUserRepository.save(AdminUser.builder().roles(Collections.singletonList("TEST")).userName("junit test").userEmail("oneoxygen@gmail.com").uid("user").password("$2a$10$2/c9RrsJTxyVROo5WV2hEevbdIDNN43Z/v7.ILUcM0LNZIhUoFLwa").userStatus("ACTIVE").passwordFailCnt(0).build());
//        }
//
//        //계정 초기화
//        AdminUser adminUser = adminUserRepository.findByUid("user").get();
//        adminUser.setPasswordFailCnt(0);
//        adminUser.setUserStatus("ACTIVE");
//        adminUserRepository.save(adminUser);
//    }

    @Test
    @DisplayName("잘못된 username 실패")
    public void userNotFound() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(
                AccountCredentials.builder().username("abc").password("user").build());

        this.mockMvc.perform(post("/auth")
                        .content(jsonBody)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorCode").value("S001"))
                .andExpect(jsonPath("$.errorMessage").value("user not found"));

    }

    @Test
    @DisplayName("잘못된 패스워드 실패")
    public void passwordFail() throws Exception {


        String jsonBody = objectMapper.writeValueAsString(
                AccountCredentials.builder().username("user").password("abc").build());


        for(int i = 1; i <=5 ; i++) {
            this.mockMvc.perform(post("/auth")
                            .content(jsonBody)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.errorCode").value("S001"))
                    .andExpect(jsonPath("$.errorMessage").value("Authentication failed"))
                    .andExpect(jsonPath("$.userStatus").value(i==5 ? "PASSWORD_LOCK" :"ACTIVE"))
                    .andExpect(jsonPath("$.passwordFailCnt").value(i));
        }
    }

    @Test
    @DisplayName("사용자 계정 잠금")
    public void userStatusPasswordLock() throws Exception {

        //when
        String jsonBody = objectMapper.writeValueAsString(
                AccountCredentials.builder().username("user").password("user").build());

        //then
        this.mockMvc.perform(post("/auth")
                        .content(jsonBody)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorCode").value("S003"))
                .andExpect(jsonPath("$.errorMessage").value("User account is locked"));

    }

    @Test
    @DisplayName("사용자 계정 휴면")
    public void userStatusDormant() throws Exception {

        //when
        String jsonBody = objectMapper.writeValueAsString(
                AccountCredentials.builder().username("user").password("user").build());

        //then
        this.mockMvc.perform(post("/auth")
                        .content(jsonBody)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorCode").value("S003"))
                .andExpect(jsonPath("$.errorMessage").value("User is disabled"));

    }

    @Test
    @DisplayName("인증 성공")
    public void authSuccess() throws Exception {

        //when
        String jsonBody = objectMapper.writeValueAsString(
                AccountCredentials.builder().username("user").password("user").build());
        //then
        this.mockMvc.perform(post("/auth")
                        .content(jsonBody)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithUserDetails("user")
    public void authOTP() throws Exception {
        this.mockMvc.perform(post("/auth/otp")).andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithUserDetails("test")
    public void testAuthOTP() throws Exception {
        this.mockMvc.perform(post("/auth/skip")).andDo(print())
                .andExpect(status().isOk());

    }


}
