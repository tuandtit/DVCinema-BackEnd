package com.cinema.booking_app.movie.service;

import com.cinema.booking_app.movie.dto.request.create.GenreRequestDto;
import com.cinema.booking_app.movie.dto.response.GenreResponseDto;

import java.util.List;

public interface GenreService {
    GenreResponseDto create(GenreRequestDto dto);
    GenreResponseDto update(Long id, GenreRequestDto dto);
    void delete(Long id);
    List<GenreResponseDto> getAll();
}
