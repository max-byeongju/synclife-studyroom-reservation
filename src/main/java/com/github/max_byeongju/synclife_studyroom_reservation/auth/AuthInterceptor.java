package com.github.max_byeongju.synclife_studyroom_reservation.auth;

import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.ErrorCode;
import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.SynclifeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_INFO_ATTRIBUTE = "authInfo";

    private final SimpleTokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        String token = parseTokenFromHeader(authorizationHeader);

        AuthInfo authInfo = tokenService.parseToken(token);
        request.setAttribute(AUTH_INFO_ATTRIBUTE, authInfo);
        return true;
    }

    private String parseTokenFromHeader(String header) {
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
