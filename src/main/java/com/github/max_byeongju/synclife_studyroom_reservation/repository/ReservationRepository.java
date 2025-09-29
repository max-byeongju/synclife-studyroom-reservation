package com.github.max_byeongju.synclife_studyroom_reservation.repository;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT * FROM reservation r " +
            "WHERE r.reservation_time && tsrange(:startOfDay, :endOfDay, '[)') " +
            "ORDER BY r.room_id, lower(r.reservation_time)",
            nativeQuery = true)
    List<Reservation> findByDate(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);
}
