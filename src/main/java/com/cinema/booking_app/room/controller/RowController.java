package com.cinema.booking_app.room.controller;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.room.dto.request.create.RowRequestDto;
import com.cinema.booking_app.room.dto.request.update.RowUpdateDto;
import com.cinema.booking_app.room.dto.response.RowResponseDto;
import com.cinema.booking_app.room.service.RowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rows")
@RequiredArgsConstructor
public class RowController {
    private final RowService service;

    @PostMapping
    public Response<RowResponseDto> create(@Valid @RequestBody RowRequestDto dto) {
        return Response.created(service.create(dto));
    }

    @PutMapping("/{id}")
    public Response<RowResponseDto> update(@PathVariable Long id,
                                           @Valid @RequestBody RowUpdateDto dto) {
        return Response.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.noContent();
    }

    @GetMapping("/{id}")
    public Response<RowResponseDto> getById(@PathVariable Long id) {
        return Response.ok(service.getById(id));
    }

    @GetMapping("")
    public Response<List<RowResponseDto>> getAll() {
        return Response.ok(service.getAll());
    }
}
