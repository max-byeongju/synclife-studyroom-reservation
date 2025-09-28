package com.github.max_byeongju.synclife_studyroom_reservation.domain;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "reservation_time", columnDefinition = "tsrange")
    private Range<LocalDateTime> reservationTime;

    @Builder
    public Reservation(User user, Room room, LocalDateTime startAt, LocalDateTime endAt) {
        this.user = user;
        this.room = room;
        this.reservationTime = Range.closedOpen(startAt, endAt);
    }
}
