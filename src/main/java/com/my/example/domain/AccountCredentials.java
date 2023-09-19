package com.my.example.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class AccountCredentials{
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
