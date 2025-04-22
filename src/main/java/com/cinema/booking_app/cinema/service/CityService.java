package com.cinema.booking_app.cinema.service;

import com.cinema.booking_app.cinema.dto.request.CityRequestDto;
import com.cinema.booking_app.cinema.dto.response.CityResponseDto;

import java.util.List;

public interface CityService {
    CityResponseDto create(CityRequestDto dto);

    CityResponseDto update(Long id, CityRequestDto dto);

    void delete(Long id);

    CityResponseDto getById(Long id);

    List<CityResponseDto> getAll();
}
