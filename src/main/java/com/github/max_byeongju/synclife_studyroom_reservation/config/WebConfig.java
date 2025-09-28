package com.github.max_byeongju.synclife_studyroom_reservation.config;

import com.github.max_byeongju.synclife_studyroom_reservation.auth.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // API 경로에만 적용
                .excludePathPatterns("/api/auth/**"); // 인증 관련 경로는 제외 (필요시)
    }
}