package com.cinema.booking_app.room.service;

import com.cinema.booking_app.room.dto.request.create.SeatRequestDto;
import com.cinema.booking_app.room.dto.request.update.SeatUpdateDto;
import com.cinema.booking_app.room.dto.response.SeatResponseDto;

import java.util.List;

public interface SeatService {
    SeatResponseDto create(SeatRequestDto dto);

    SeatResponseDto update(Long id, SeatUpdateDto dto);

    void delete(Long id);

    SeatResponseDto getById(Long id);

    List<SeatResponseDto> getAll();

    void holdSeat(Long userId, Long seatId);

    void releaseSeatById(List<Long> seatIds);
}
