package com.cinema.booking_app.movie.service;

import com.cinema.booking_app.movie.dto.request.create.MovieRequestDto;
import com.cinema.booking_app.movie.dto.request.search.GetMoviesByDateAndCinemaRequest;
import com.cinema.booking_app.movie.dto.request.search.MovieSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.MovieUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.MovieResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;


public interface MovieService {
    MovieResponseDto create(MovieRequestDto dto);

    MovieResponseDto update(MovieUpdateRequestDto dto);

    void delete(Long id);

    MovieResponseDto getById(Long id);

    Page<MovieResponseDto> getAll(final MovieSearchRequest request);

    List<MovieResponseDto> getMoviesByDateAndCinema(GetMoviesByDateAndCinemaRequest request);
}
