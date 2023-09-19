package com.my.example.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="`email_contents`")
public class EmailContents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false, unique = true)
    private String subject;

    @Column(nullable = false)
    private String content;

}
