package com.cinema.booking_app.cinema.service;

import com.cinema.booking_app.cinema.dto.request.CinemaRequestDto;
import com.cinema.booking_app.cinema.dto.request.CinemaUpdateDto;
import com.cinema.booking_app.cinema.dto.response.CinemaResponseDto;

import java.util.List;

public interface CinemaService {
    CinemaResponseDto create(CinemaRequestDto dto);

    CinemaResponseDto update(Long id, CinemaUpdateDto dto);

    void delete(Long id);

    CinemaResponseDto getById(Long id);

    List<CinemaResponseDto> getAll();
}
