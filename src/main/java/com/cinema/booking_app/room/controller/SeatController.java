package com.cinema.booking_app.room.controller;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.room.dto.request.create.SeatRequestDto;
import com.cinema.booking_app.room.dto.request.update.SeatUpdateDto;
import com.cinema.booking_app.room.dto.response.SeatResponseDto;
import com.cinema.booking_app.room.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService service;

    @PostMapping
    public Response<SeatResponseDto> create(@Valid @RequestBody SeatRequestDto dto) {
        return Response.created(service.create(dto));
    }

    @PutMapping("/{id}")
    public Response<SeatResponseDto> update(@PathVariable Long id,
                                            @Valid @RequestBody SeatUpdateDto dto) {
        return Response.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.noContent();
    }

    @GetMapping("/{id}")
    public Response<SeatResponseDto> getById(@PathVariable Long id) {
        return Response.ok(service.getById(id));
    }

    @GetMapping("")
    public Response<List<SeatResponseDto>> getAll() {
        return Response.ok(service.getAll());
    }
}
