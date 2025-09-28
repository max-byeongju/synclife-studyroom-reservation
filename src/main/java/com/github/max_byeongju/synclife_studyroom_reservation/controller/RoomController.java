package com.github.max_byeongju.synclife_studyroom_reservation.controller;

import com.github.max_byeongju.synclife_studyroom_reservation.auth.AuthInfo;
import com.github.max_byeongju.synclife_studyroom_reservation.auth.AuthInterceptor;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomAvailabilityResponseDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomCreateRequestDto;
import com.github.max_byeongju.synclife_studyroom_reservation.dto.room.RoomResponseDto;
import com.github.max_byeongju.synclife_studyroom_reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/rooms")
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomCreateRequestDto request,
                                                      @RequestAttribute(AuthInterceptor.AUTH_INFO_ATTRIBUTE) AuthInfo authInfo) {
        RoomResponseDto response = roomService.createRoom(authInfo.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomAvailabilityResponseDto>> getRoomAvailability(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RoomAvailabilityResponseDto> response = roomService.getRoomsAvailability(date);
        return ResponseEntity.ok(response);
    }
}
