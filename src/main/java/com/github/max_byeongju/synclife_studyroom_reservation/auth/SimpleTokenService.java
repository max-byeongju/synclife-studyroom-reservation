package com.github.max_byeongju.synclife_studyroom_reservation.auth;

import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.ErrorCode;
import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.SynclifeException;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import org.springframework.stereotype.Service;

@Service
public class SimpleTokenService {

    private static final String ADMIN_TOKEN = "admin-token";
    private static final String USER_TOKEN_PREFIX = "user-token-";
    private static final Long ADMIN_USER_ID = 1L;

    public AuthInfo parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new SynclifeException(ErrorCode.UNAUTHORIZED);
        }

        // admin-token -> ADMIN 권한
        if (ADMIN_TOKEN.equals(token)) {
            return new AuthInfo(ADMIN_USER_ID, Role.ADMIN);
        }

        // user-token-{id} -> USER 권한
        if (token.startsWith(USER_TOKEN_PREFIX)) {
            try {
                String userIdStr = token.substring(USER_TOKEN_PREFIX.length());
                Long userId = Long.parseLong(userIdStr);
                return new AuthInfo(userId, Role.USER);
            } catch (NumberFormatException e) {
                throw new SynclifeException(ErrorCode.UNAUTHORIZED);
            }
        }

        throw new SynclifeException(ErrorCode.UNAUTHORIZED);
    }
}
