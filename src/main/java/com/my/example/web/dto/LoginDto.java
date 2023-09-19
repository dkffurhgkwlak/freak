package com.my.example.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginDto extends BaseExceptionDto {
    private String userName;
    private String role;
    private List<String> pages = new ArrayList<String>();
    private String userStatus;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int passwordFailCnt;
}
