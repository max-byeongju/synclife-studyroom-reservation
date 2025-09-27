package com.github.max_byeongju.synclife_studyroom_reservation.repository;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.room = :room AND r.endAt > :startAt AND r.startAt < :endAt")
    boolean existsConflictingReservation(
            @Param("room") Room room,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
