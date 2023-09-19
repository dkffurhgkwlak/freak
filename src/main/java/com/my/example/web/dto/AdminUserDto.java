package com.my.example.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserDto {
    private String uid;
    private String password;
    private int passwordFailCnt;
    private String userStatus;
    private String userEmail;
    private String userName;
}
