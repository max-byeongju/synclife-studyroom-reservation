package com.github.max_byeongju.synclife_studyroom_reservation.repository;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
