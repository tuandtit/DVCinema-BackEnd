package com.cinema.booking_app.room.service;

import com.cinema.booking_app.room.dto.request.create.RoomRequestDto;
import com.cinema.booking_app.room.dto.request.update.RoomUpdateDto;
import com.cinema.booking_app.room.dto.response.RoomResponseDto;

import java.util.List;

public interface RoomService {
    RoomResponseDto create(RoomRequestDto dto);

    RoomResponseDto update(Long id, RoomUpdateDto dto);

    void delete(Long id);

    RoomResponseDto getById(Long id);

    List<RoomResponseDto> getAll();
}
