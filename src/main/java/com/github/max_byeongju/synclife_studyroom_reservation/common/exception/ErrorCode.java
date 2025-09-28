package com.github.max_byeongju.synclife_studyroom_reservation.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "예약 시작 시간은 종료 시간보다 빨라야 합니다."),

    // 403 FORBIDDEN
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "요청에 대한 권한이 없습니다."),

    // 404 NOT_FOUND
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 409 CONFLICT
    RESERVATION_CONFLICT(HttpStatus.CONFLICT, "해당 시간에 이미 예약이 존재합니다.");

    private final HttpStatus status;
    private final String message;
}
