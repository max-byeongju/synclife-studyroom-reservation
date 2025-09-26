package com.github.max_byeongju.synclife_studyroom_reservation.domain;

import jakarta.persistence.*;

@Entity
public class User {

    @Id @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}