package com.github.max_byeongju.synclife_studyroom_reservation.repository;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
