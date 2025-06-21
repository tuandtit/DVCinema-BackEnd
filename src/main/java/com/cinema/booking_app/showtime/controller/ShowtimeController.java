package com.cinema.booking_app.showtime.controller;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.showtime.dto.request.ShowtimeRequestDto;
import com.cinema.booking_app.showtime.dto.response.ShowtimeResponseDto;
import com.cinema.booking_app.showtime.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public Response<ShowtimeResponseDto> create(@RequestBody @Valid ShowtimeRequestDto dto) {
        return Response.ok(showtimeService.create(dto));
    }

    @GetMapping("/get-all")
    public Response<List<ShowtimeResponseDto>> getAll() {
        return Response.ok(showtimeService.getAll());
    }

    @GetMapping("/{id}")
    public Response<ShowtimeResponseDto> getById(@PathVariable Long id) {
        return Response.ok(showtimeService.getById(id));
    }

    @GetMapping("")
    public Response<List<ShowtimeResponseDto>> getByMovieIdAndCinemaId(@RequestParam("movieId") Long movieId, @RequestParam("cinemaId") Long cinemaId) {
        return Response.ok(showtimeService.getByMovieIdAndCinemaId(movieId, cinemaId));
    }

    @GetMapping("/filter-showtime")
    public Response<List<ShowtimeResponseDto>> getByCinemaIdAndShowDate(@RequestParam(required = false) Long cinemaId, @RequestParam(required = false) LocalDate showDate) {
        return Response.ok(showtimeService.findByCinemaIdAndShowDate(cinemaId, showDate));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        showtimeService.delete(id);
        return Response.noContent();
    }
}