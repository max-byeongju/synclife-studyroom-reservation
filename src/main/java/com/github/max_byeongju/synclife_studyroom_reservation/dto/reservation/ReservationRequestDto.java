package com.github.max_byeongju.synclife_studyroom_reservation.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    private Long roomId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
