package com.github.max_byeongju.synclife_studyroom_reservation.service;

import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.ErrorCode;
import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.SynclifeException;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomAvailabilityResponseDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomCreateRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomResponseDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.TimeSlotDto;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.ReservationRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.RoomRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomResponseDto createRoom(Long userId, RoomCreateRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SynclifeException(ErrorCode.ENTITY_NOT_FOUND));

        if (!user.getRole().equals(Role.ADMIN)) {
            throw new SynclifeException(ErrorCode.ACCESS_DENIED);
        }

        Room room = Room.builder()
                .name(request.getName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .build();

        Room savedRoom = roomRepository.save(room);

        return RoomResponseDto.from(savedRoom);
    }


    public List<RoomAvailabilityResponseDto> getRoomsAvailability(LocalDate date) {
        List<Room> allRooms = roomRepository.findAll();
        List<Reservation> reservationsOnDate = reservationRepository.findByDateNative(date); // 네이티브 쿼리 사용

        Map<Long, List<Reservation>> reservationsByRoomId = new HashMap<>();
        for (Reservation reservation : reservationsOnDate) {
            Long roomId = reservation.getRoom().getId();
            reservationsByRoomId.computeIfAbsent(roomId, k -> new ArrayList<>()).add(reservation);
        }

        List<RoomAvailabilityResponseDto> result = new ArrayList<>(); // 최종 결과를 담을 리스트

        for (Room room : allRooms) {
            List<Reservation> roomReservations = reservationsByRoomId.getOrDefault(room.getId(), Collections.emptyList());

            roomReservations.sort(Comparator.comparing(r -> r.getReservationTime().lower()));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            List<TimeSlotDto> availableSlots = calculateAvailableSlots(roomReservations, startOfDay, endOfDay);

            RoomAvailabilityResponseDto dto = RoomAvailabilityResponseDto.of(room, roomReservations, availableSlots);
            result.add(dto);
        }

        return result;
    }

    private List<TimeSlotDto> calculateAvailableSlots(List<Reservation> reservations, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        List<TimeSlotDto> slots = new ArrayList<>();
        LocalDateTime timeline = startOfDay;

        for (Reservation reservation : reservations) {
            LocalDateTime reservationStart = reservation.getReservationTime().lower();
            LocalDateTime reservationEnd = reservation.getReservationTime().upper();

            if (timeline.isBefore(reservationStart)) {
                slots.add(new TimeSlotDto(timeline, reservationStart));
            }
            timeline = reservationEnd;
        }

        if (timeline.isBefore(endOfDay)) {
            slots.add(new TimeSlotDto(timeline, endOfDay));
        }

        return slots;
    }
}
