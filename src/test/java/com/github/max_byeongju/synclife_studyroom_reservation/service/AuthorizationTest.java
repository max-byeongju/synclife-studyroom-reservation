package com.github.max_byeongju.synclife_studyroom_reservation.service;

import com.github.max_byeongju.synclife_studyroom_reservation.common.exception.SynclifeException;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.reservation.ReservationRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomCreateRequestDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class AuthorizationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User adminUser;
    private User normalUser1;
    private User normalUser2;
    private Room testRoom;

    @BeforeEach
    @Transactional
    void setUp() {
        // 기존 데이터 정리
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // ADMIN 사용자 생성
        adminUser = User.builder()
                .name("관리자")
                .role(Role.ADMIN)
                .build();
        userRepository.save(adminUser);

        // 일반 사용자들 생성
        normalUser1 = User.builder()
                .name("일반사용자1")
                .role(Role.USER)
                .build();
        userRepository.save(normalUser1);

        normalUser2 = User.builder()
                .name("일반사용자2")
                .role(Role.USER)
                .build();
        userRepository.save(normalUser2);

        // 테스트용 방 생성
        testRoom = Room.builder()
                .name("테스트룸")
                .location("1층")
                .capacity(10)
                .build();
        roomRepository.save(testRoom);

        log.info("권한 테스트 데이터 생성 완료");
    }

    @Test
    @DisplayName("ADMIN 사용자는 회의실을 생성할 수 있고, 일반 사용자는 생성할 수 없다")
    void roomCreationRequiresAdminRole() {
        // Given
        RoomCreateRequestDto roomRequest = new RoomCreateRequestDto("새로운 회의실", "2층", 8);

        // When & Then
        assertThatCode(() -> roomService.createRoom(adminUser.getId(), roomRequest))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> roomService.createRoom(normalUser1.getId(), roomRequest))
                .isInstanceOf(SynclifeException.class)
                .hasMessageContaining("ADMIN 권한이 필요합니다.");

        assertThat(roomRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자는 자신의 예약을 취소할 수 있다")
    void userCanCancelOwnReservation() {
        // Given
        LocalDateTime startAt = LocalDateTime.now().plusHours(1);
        LocalDateTime endAt = startAt.plusHours(2);
        ReservationRequestDto request = new ReservationRequestDto(testRoom.getId(), startAt, endAt);
        reservationService.createReservation(normalUser1.getId(), request);
        Reservation reservation = reservationRepository.findAll().get(0);
        Long reservationId = reservation.getId();

        // When & Then
        assertThatCode(() -> reservationService.cancelReservation(normalUser1.getId(), reservationId))
                .doesNotThrowAnyException();

        assertThat(reservationRepository.existsById(reservationId)).isFalse();
    }

    @Test
    @DisplayName("사용자는 다른 사용자의 예약을 취소할 수 없다")
    void userCannotCancelOthersReservation() {
        // Given
        LocalDateTime startAt = LocalDateTime.now().plusHours(3);
        LocalDateTime endAt = startAt.plusHours(2);
        ReservationRequestDto request = new ReservationRequestDto(testRoom.getId(), startAt, endAt);
        reservationService.createReservation(normalUser1.getId(), request);
        Reservation reservation = reservationRepository.findAll().get(0);
        Long reservationId = reservation.getId();

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(normalUser2.getId(), reservationId))
                .isInstanceOf(SynclifeException.class)
                .hasMessageContaining("자신의 예약만 취소할 수 있습니다.");

        assertThat(reservationRepository.existsById(reservationId)).isTrue();
    }

    @Test
    @DisplayName("ADMIN은 다른 사용자의 예약을 취소할 수 있다")
    void adminCanCancelAnyReservation() {
        // Given
        LocalDateTime startAt = LocalDateTime.now().plusHours(5);
        LocalDateTime endAt = startAt.plusHours(2);
        ReservationRequestDto request = new ReservationRequestDto(testRoom.getId(), startAt, endAt);
        reservationService.createReservation(normalUser1.getId(), request);
        Reservation reservation = reservationRepository.findAll().get(0);
        Long reservationId = reservation.getId();

        // When & Then
        assertThatCode(() -> reservationService.cancelReservation(adminUser.getId(), reservationId))
                .doesNotThrowAnyException();

        assertThat(reservationRepository.existsById(reservationId)).isFalse();
    }
}
