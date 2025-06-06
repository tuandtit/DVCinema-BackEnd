package com.cinema.booking_app.booking.service;

import com.cinema.booking_app.common.base.dto.response.BaseDto;
import com.cinema.booking_app.booking.dto.response.SeatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface TicketService {

    @Transactional
    BaseDto holdSeat(Long userId, Long seatId, Long showtimeId, BigDecimal ticketPrice);

    @Transactional
    List<BaseDto> extendSeatHoldTime(Set<Long> seatShowtimeIds, Long showtimeId);

    void releaseSeatByIds(List<Long> seatShowtimeIds, Long showtimeId);

    List<SeatsDto> getAllByShowtimeId(Long showtimeId);
}