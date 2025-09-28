package com.github.max_byeongju.synclife_studyroom_reservation.dto.room;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import lombok.Builder;

public class RoomResponseDto {

    private final Long id;
    private final String name;
    private final String location;
    private final Integer capacity;

    @Builder
    private RoomResponseDto(Long id, String name, String location, Integer capacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public static RoomResponseDto from(Room room) {
        return RoomResponseDto.builder()
                .id(room.getId())
                .name(room.getName())
                .location(room.getLocation())
                .capacity(room.getCapacity())
                .build();
    }
}
