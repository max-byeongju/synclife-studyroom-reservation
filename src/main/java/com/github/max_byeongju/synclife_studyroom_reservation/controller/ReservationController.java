package com.github.max_byeongju.synclife_studyroom_reservation.controller;

import com.github.max_byeongju.synclife_studyroom_reservation.auth.AuthInfo;
import com.github.max_byeongju.synclife_studyroom_reservation.auth.AuthInterceptor;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.reservation.ReservationRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations")
    public ResponseEntity<Void> createReservation(@RequestBody ReservationRequestDto request,
                                                  @RequestAttribute(AuthInterceptor.AUTH_INFO_ATTRIBUTE) AuthInfo authInfo) {
        reservationService.createReservation(authInfo.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id,
                                                  @RequestAttribute(AuthInterceptor.AUTH_INFO_ATTRIBUTE) AuthInfo authInfo) {
        reservationService.cancelReservation(authInfo.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
