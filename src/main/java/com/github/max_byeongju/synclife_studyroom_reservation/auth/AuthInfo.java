package com.github.max_byeongju.synclife_studyroom_reservation.auth;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthInfo {
    private Long userId;
    private Role role;
}
