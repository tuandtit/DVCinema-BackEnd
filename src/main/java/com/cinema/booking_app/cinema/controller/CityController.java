package com.cinema.booking_app.cinema.controller;

import com.cinema.booking_app.cinema.dto.request.CityRequestDto;
import com.cinema.booking_app.cinema.dto.response.CityResponseDto;
import com.cinema.booking_app.cinema.service.CityService;
import com.cinema.booking_app.common.base.dto.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService service;

    @PostMapping
    public Response<CityResponseDto> create(@Valid @RequestBody CityRequestDto dto) {
        return Response.created(service.create(dto));
    }

    @PutMapping("/{id}")
    public Response<CityResponseDto> update(@PathVariable Long id,
                                            @Valid @RequestBody CityRequestDto dto) {
        return Response.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.noContent();
    }

    @GetMapping("/{id}")
    public Response<CityResponseDto> getById(@PathVariable Long id) {
        return Response.ok(service.getById(id));
    }

    @GetMapping("")
    public Response<List<CityResponseDto>> getAll() {
        return Response.ok(service.getAll());
    }
}
