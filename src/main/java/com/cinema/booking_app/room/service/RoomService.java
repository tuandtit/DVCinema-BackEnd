package com.cinema.booking_app.room.service;

import com.cinema.booking_app.room.dto.request.create.RoomRequestDto;
import com.cinema.booking_app.room.dto.request.update.RoomUpdateDto;
import com.cinema.booking_app.room.dto.response.RoomResponseDto;
import com.cinema.booking_app.booking.dto.response.SeatsDto;

import java.util.List;

public interface RoomService {
    RoomResponseDto create(RoomRequestDto dto);

    RoomResponseDto update(Long id, RoomUpdateDto dto);

    void delete(Long id);

    List<SeatsDto> getSeatsByRoomId(Long id);

    List<RoomResponseDto> getAll();
}
