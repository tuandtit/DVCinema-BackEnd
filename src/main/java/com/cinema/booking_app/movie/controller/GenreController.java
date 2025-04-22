package com.cinema.booking_app.movie.controller;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.movie.dto.request.create.GenreRequestDto;
import com.cinema.booking_app.movie.dto.response.GenreResponseDto;
import com.cinema.booking_app.movie.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public Response<GenreResponseDto> create(@Valid @RequestBody GenreRequestDto dto) {
        return Response.created(genreService.create(dto));
    }

    @PutMapping("/{id}")
    public Response<GenreResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody GenreRequestDto dto) {
        return Response.ok(genreService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        genreService.delete(id);
        return Response.noContent();
    }

    @GetMapping
    public Response<List<GenreResponseDto>> getAll() {
        return Response.ok(genreService.getAll());
    }
}
