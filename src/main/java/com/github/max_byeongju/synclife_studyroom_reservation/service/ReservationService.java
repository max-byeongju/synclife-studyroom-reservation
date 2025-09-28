package com.github.max_byeongju.synclife_studyroom_reservation.service;

import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.ErrorCode;
import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.SynclifeException;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.reservation.ReservationRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.ReservationRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.RoomRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public void createReservation(Long userId, ReservationRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SynclifeException(ErrorCode.ENTITY_NOT_FOUND));
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new SynclifeException(ErrorCode.ENTITY_NOT_FOUND));

        if (request.getStartAt().isAfter(request.getEndAt()) || request.getStartAt().isEqual(request.getEndAt())) {
            throw new SynclifeException(ErrorCode.INVALID_RESERVATION_TIME);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();

        try {
            reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new SynclifeException(ErrorCode.RESERVATION_CONFLICT);
        }
    }

    public void cancelReservation(Long userId, Long reservationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SynclifeException(ErrorCode.ENTITY_NOT_FOUND));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new SynclifeException(ErrorCode.ENTITY_NOT_FOUND));

        Long ownerId = reservation.getUser().getId();
        boolean isAdmin = user.getRole().equals(Role.ADMIN);
        boolean isOwner = user.getId().equals(ownerId);

        if (!isAdmin && !isOwner) {
            throw new SynclifeException(ErrorCode.ACCESS_DENIED);
        }

        reservationRepository.delete(reservation);
    }
}
