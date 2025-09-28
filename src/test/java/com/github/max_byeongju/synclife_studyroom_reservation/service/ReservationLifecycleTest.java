package com.github.max_byeongju.synclife_studyroom_reservation.service;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.reservation.ReservationRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.ReservationRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.RoomRepository;
import com.github.max_byeongju.synclife_studyroom_reservation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class ReservationLifecycleTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User user1;
    private User user2;
    private Room testRoom;

    @BeforeEach
    @Transactional
    void setUp() {
        // 기존 데이터 정리
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        user1 = User.builder()
                .name("사용자1")
                .role(Role.USER)
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .name("사용자2")
                .role(Role.USER)
                .build();
        user2 = userRepository.save(user2);

        // 테스트용 방 생성
        testRoom = Room.builder()
                .name("테스트룸")
                .location("1층")
                .capacity(10)
                .build();
        testRoom = roomRepository.save(testRoom);

        log.info("테스트 데이터 생성 완료 - User1: {}, User2: {}, Room: {}",
                user1.getId(), user2.getId(), testRoom.getId());
    }

    @Test
    @DisplayName("예약 취소 후, 동일한 시간대에 다른 사용자가 재예약할 수 있다")
    void rebookingSucceedsAfterCancellation() {
        // Given
        LocalDateTime startAt = LocalDateTime.now().plusHours(2);
        LocalDateTime endAt = startAt.plusHours(3);
        ReservationRequestDto request = new ReservationRequestDto(testRoom.getId(), startAt, endAt);

        // When
        reservationService.createReservation(user1.getId(), request);

        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(1);
        Long firstReservationId = reservations.get(0).getId();
        log.info("첫 번째 예약 생성됨 - ID: {}", firstReservationId);

        reservationService.cancelReservation(user1.getId(), firstReservationId);

        assertThat(reservationRepository.existsById(firstReservationId)).isFalse();
        log.info("첫 번째 예약 취소됨");

        assertThatCode(() -> reservationService.createReservation(user2.getId(), request))
                .doesNotThrowAnyException();

        // Then
        List<Reservation> newReservations = reservationRepository.findAll();
        assertThat(newReservations).hasSize(1);
        assertThat(newReservations.get(0).getUser().getId()).isEqualTo(user2.getId());
        assertThat(newReservations.get(0).getId()).isNotEqualTo(firstReservationId);
        log.info("동일 시간대 재예약 성공 - 새 예약 ID: {}", newReservations.get(0).getId());
    }
}
