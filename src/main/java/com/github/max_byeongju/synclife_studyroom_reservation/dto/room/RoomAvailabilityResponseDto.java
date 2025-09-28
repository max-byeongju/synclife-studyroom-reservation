package com.github.max_byeongju.synclife_studyroom_reservation.dto.room;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RoomAvailabilityResponseDto {

    private final Long roomId;
    private final String roomName;
    private final List<TimeSlotDto> reservations;
    private final List<TimeSlotDto> availableSlots;

    @Builder
    private RoomAvailabilityResponseDto(Long roomId, String roomName, List<TimeSlotDto> reservations, List<TimeSlotDto> availableSlots) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.reservations = reservations;
        this.availableSlots = availableSlots;
    }

    public static RoomAvailabilityResponseDto of(Room room, List<Reservation> roomReservations, List<TimeSlotDto> availableSlots) {

        // 예약 목록(Reservation Entity List)을 DTO 목록(TimeSlotDto List)으로 변환
        List<TimeSlotDto> reservationSlots = new ArrayList<>();

        // 2. roomReservations 리스트를 순회합니다.
        for (Reservation reservation : roomReservations) {
            // 3. 각 Reservation 객체를 TimeSlotDto로 변환합니다.
            TimeSlotDto timeSlotDto = TimeSlotDto.from(reservation);

            // 4. 변환된 DTO를 새로운 리스트에 추가합니다.
            reservationSlots.add(timeSlotDto);
        }

        return RoomAvailabilityResponseDto.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .reservations(reservationSlots)
                .availableSlots(availableSlots)
                .build();
    }
}
