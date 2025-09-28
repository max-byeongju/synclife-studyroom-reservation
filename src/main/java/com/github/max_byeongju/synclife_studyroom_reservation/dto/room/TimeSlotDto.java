package com.github.max_byeongju.synclife_studyroom_reservation.dto.room;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TimeSlotDto {

    private final LocalDateTime startAt;
    private final LocalDateTime endAt;

    public TimeSlotDto(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static TimeSlotDto from(Reservation reservation) {
        // Range 객체의 lowerEndpoint()와 upperEndpoint()를 사용하여 시작/종료 시간을 가져옵니다.
        return new TimeSlotDto(
                reservation.getReservationTime().lower(),
                reservation.getReservationTime().upper()
        );
    }
}
