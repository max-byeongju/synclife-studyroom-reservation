package com.github.max_byeongju.synclife_studyroom_reservation.service;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Role;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class ConcurrentReservationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room testRoom;
    private List<User> testUsers;

    @BeforeEach
    @Transactional
    void setUp() {
        // 기존 데이터 정리
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 방 생성
        testRoom = Room.builder()
                .name("테스트룸")
                .location("1층")
                .capacity(10)
                .build();
        testRoom = roomRepository.save(testRoom);

        // 테스트용 사용자 10명 생성
        testUsers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .name("사용자" + i)
                    .role(Role.USER)
                    .build();
            testUsers.add(userRepository.save(user));
        }

        log.info("테스트 데이터 생성 완료 - 방 ID: {}, 사용자 수: {}", testRoom.getId(), testUsers.size());
    }

    @Test
    @DisplayName("겹치는 시간대에 대한 10개의 동시 예약 요청 중 1개만 성공해야 한다")
    void concurrentConflictingReservationsAllowOne() throws InterruptedException {
        // Given
        LocalDateTime startAt = LocalDateTime.now().plusHours(1);
        LocalDateTime endAt = startAt.plusHours(2);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(10);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        // When
        for (int i = 0; i < 10; i++) {
            final int userIndex = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    ReservationRequestDto request = new ReservationRequestDto(
                            testRoom.getId(),
                            startAt,
                            endAt
                    );

                    reservationService.createReservation(
                            testUsers.get(userIndex).getId(),
                            request
                    );

                    successCount.incrementAndGet();
                    log.info("예약 성공 - 사용자: {}", testUsers.get(userIndex).getName());

                } catch (Exception e) {
                    failCount.incrementAndGet();
                    exceptions.add(e);
                    log.info("예약 실패 - 사용자: {}, 오류: {}",
                            testUsers.get(userIndex).getName(), e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        boolean finished = endLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();

        // Then
        assertThat(finished).isTrue();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(9);

        long reservationCount = reservationRepository.count();
        assertThat(reservationCount).isEqualTo(1);

        log.info("=== 동시성 테스트 결과 ===");
        log.info("성공한 예약: {}개", successCount.get());
        log.info("실패한 예약: {}개", failCount.get());
        log.info("DB에 저장된 예약: {}개", reservationCount);
        log.info("발생한 예외 수: {}개", exceptions.size());

        boolean hasConstraintViolation = exceptions.stream()
                .anyMatch(e -> e.getMessage().contains("reservation_room_time_excl") ||
                        e.getMessage().contains("duplicate") ||
                        e.getMessage().contains("constraint"));

        assertThat(hasConstraintViolation).isTrue();
    }
}