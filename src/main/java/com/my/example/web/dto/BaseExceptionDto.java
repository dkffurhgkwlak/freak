package com.my.example.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class BaseExceptionDto {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorCode;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorMessage;
}