package com.my.example.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class AdminUserRoleDto {
    private String name;
    private List<String> pages = new ArrayList<>();
}
