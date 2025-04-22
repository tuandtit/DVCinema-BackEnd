package com.cinema.booking_app.cinema.controller;

import com.cinema.booking_app.cinema.dto.request.CinemaRequestDto;
import com.cinema.booking_app.cinema.dto.request.CinemaUpdateDto;
import com.cinema.booking_app.cinema.dto.response.CinemaResponseDto;
import com.cinema.booking_app.cinema.service.CinemaService;
import com.cinema.booking_app.common.base.dto.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService service;

    @PostMapping
    public Response<CinemaResponseDto> create(@Valid @RequestBody CinemaRequestDto dto) {
        return Response.created(service.create(dto));
    }

    @PutMapping("/{id}")
    public Response<CinemaResponseDto> update(@PathVariable Long id,
                                              @Valid @RequestBody CinemaUpdateDto dto) {
        return Response.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.noContent();
    }

    @GetMapping("/{id}")
    public Response<CinemaResponseDto> getById(@PathVariable Long id) {
        return Response.ok(service.getById(id));
    }

    @GetMapping("")
    public Response<List<CinemaResponseDto>> getAll() {
        return Response.ok(service.getAll());
    }
}
