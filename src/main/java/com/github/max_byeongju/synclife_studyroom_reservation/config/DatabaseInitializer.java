package com.github.max_byeongju.synclife_studyroom_reservation.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final DataSource dataSource;

    @PostConstruct
    public void addExcludeConstraint() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // btree_gist 확장 활성화
            statement.execute("CREATE EXTENSION IF NOT EXISTS btree_gist");
            log.info("btree_gist 확장이 활성화되었습니다.");

            // 기존 제약조건 확인 후 추가
            try {
                statement.execute(
                        "ALTER TABLE reservation " +
                                "ADD CONSTRAINT reservation_room_time_excl " +
                                "EXCLUDE USING gist (room_id WITH =, reservation_time WITH &&)"
                );
                log.info("EXCLUDE 제약 조건이 성공적으로 추가되었습니다.");
            } catch (SQLException e) {
                if (e.getMessage().contains("already exists")) {
                    log.info("EXCLUDE 제약 조건이 이미 존재하여 추가를 건너뜁니다.");
                } else {
                    throw e;
                }
            }

        } catch (SQLException e) {
            log.error("EXCLUDE 제약 조건 설정 중 오류가 발생했습니다.", e);
        }
    }
}