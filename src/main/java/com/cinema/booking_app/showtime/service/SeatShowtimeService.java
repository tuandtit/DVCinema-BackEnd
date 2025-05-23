package com.cinema.booking_app.showtime.service;

import com.cinema.booking_app.common.base.dto.response.BaseDto;
import com.cinema.booking_app.showtime.dto.response.SeatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatShowtimeService {

    @Transactional
    BaseDto holdSeat(Long userId, Long seatId, Long showtimeId);

    void releaseSeatByIds(Long showtimeId, List<Long> seatIds);

    List<SeatsDto> getAllByShowtimeId(Long showtimeId);
}