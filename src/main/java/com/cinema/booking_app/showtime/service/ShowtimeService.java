package com.cinema.booking_app.showtime.service;

import com.cinema.booking_app.showtime.dto.request.ShowtimeRequestDto;
import com.cinema.booking_app.showtime.dto.response.ShowtimeResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService {
    ShowtimeResponseDto create(ShowtimeRequestDto dto);

    List<ShowtimeResponseDto> getAll();

    List<ShowtimeResponseDto> findByCinemaIdAndShowDate(Long cinemaId, LocalDate showDate);

    ShowtimeResponseDto getById(Long id);

    void delete(Long id);

    List<ShowtimeResponseDto> getByMovieIdAndCinemaId(Long movieId, Long cinemaId);
}