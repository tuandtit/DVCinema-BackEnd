package com.cinema.booking_app.movie.controller;

import com.cinema.booking_app.common.base.dto.response.PagingResponse;
import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.movie.dto.request.create.MovieRequestDto;
import com.cinema.booking_app.movie.dto.request.search.GetMoviesByDateAndCinemaRequest;
import com.cinema.booking_app.movie.dto.request.search.MovieSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.MovieUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.MovieResponseDto;
import com.cinema.booking_app.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MovieResponseDto> create(@Valid MovieRequestDto dto) {
        return Response.created(movieService.create(dto));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MovieResponseDto> update(@Valid MovieUpdateRequestDto dto) {
        return Response.ok(movieService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return Response.noContent();
    }

    @GetMapping("/{id}")
    public Response<MovieResponseDto> getById(@PathVariable Long id) {
        return Response.ok(movieService.getById(id));
    }

    @PostMapping("/search")
    public Response<PagingResponse<MovieResponseDto>> getAll(@RequestBody final MovieSearchRequest request) {
        return Response.ok(PagingResponse.from(movieService.getAll(request)));
    }

    @GetMapping("/by-date")
    public Response<List<MovieResponseDto>> getByDate(@RequestBody final GetMoviesByDateAndCinemaRequest request) {
        return Response.ok(movieService.getMoviesByDateAndCinema(request));
    }
}