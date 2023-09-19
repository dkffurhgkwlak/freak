package com.my.example.web.dto;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
public class PageDto {
    private String id;
    private String path;
}
