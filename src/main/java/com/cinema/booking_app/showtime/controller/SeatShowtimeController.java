package com.cinema.booking_app.showtime.controller;

import com.cinema.booking_app.common.base.dto.response.BaseDto;
import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.showtime.dto.response.SeatsDto;
import com.cinema.booking_app.showtime.service.SeatShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/seat-by-showtime")
@RequiredArgsConstructor
public class SeatShowtimeController {

    private final SeatShowtimeService service;

    @PostMapping("/hold-seat")
    public Response<BaseDto> holdSeats(@RequestParam Long userId, @RequestParam Long seatId, @RequestParam Long showtimeId) {
        return Response.ok(service.holdSeat(userId, seatId, showtimeId));
    }

    @PostMapping("/release-seats")
    public Response<Void> releaseSeats(@RequestParam List<Long> seatShowtimeIds, @RequestParam Long showtimeId) {
        service.releaseSeatByIds(seatShowtimeIds, showtimeId);
        return Response.noContent();
    }

    @PostMapping("/extend-seat-hold-time")
    public Response<Void> extendSeatHoldTime(@RequestParam Set<Long> seatShowtimeIds, @RequestParam Long showtimeId) {
        service.extendSeatHoldTime(seatShowtimeIds, showtimeId);
        return Response.noContent();
    }

    @GetMapping("/seats-of-showtime")
    public Response<List<SeatsDto>> getAllSeatByShowtimeId(@RequestParam Long showtimeId) {
        return Response.ok(service.getAllByShowtimeId(showtimeId));
    }

}