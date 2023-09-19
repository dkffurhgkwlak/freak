package com.my.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.example.domain.AccountCredentials;
import com.my.example.service.UserDetailsServiceImpl;
import com.my.example.web.controller.LoginConroller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginConroller.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserDetailsServiceImpl service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("authentication test")
    void authTest() throws Exception {
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
}
