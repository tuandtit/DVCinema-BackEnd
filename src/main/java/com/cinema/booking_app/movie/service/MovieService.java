package com.cinema.booking_app.movie.service;

import com.cinema.booking_app.movie.dto.request.create.MovieRequestDto;
import com.cinema.booking_app.movie.dto.request.search.MovieSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.MovieUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.MovieResponseDto;
import org.springframework.data.domain.Page;


public interface MovieService {
    MovieResponseDto create(MovieRequestDto dto);

    MovieResponseDto update(Long id, MovieUpdateRequestDto dto);

    void delete(Long id);

    MovieResponseDto getById(Long id);

    Page<MovieResponseDto> getAll(final MovieSearchRequest request);
}
