package com.my.example.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="`session_save`")
public class SessionSave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String sessionKey;

    @Column(nullable = false)
    private String sessionValue;

    @Column(nullable = false)
    private LocalDateTime expireTime;
}
