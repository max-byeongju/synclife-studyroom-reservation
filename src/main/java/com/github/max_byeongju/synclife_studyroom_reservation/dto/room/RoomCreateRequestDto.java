package com.github.max_byeongju.synclife_studyroom_reservation.dto.room;

import com.github.max_byeongju.synclife_studyroom_reservation.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequestDto {

    private String name;
    private String location;
    private Integer capacity;
}
