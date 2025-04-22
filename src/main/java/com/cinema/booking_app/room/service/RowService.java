package com.cinema.booking_app.room.service;

import com.cinema.booking_app.room.dto.request.create.RowRequestDto;
import com.cinema.booking_app.room.dto.request.update.RowUpdateDto;
import com.cinema.booking_app.room.dto.response.RowResponseDto;

import java.util.List;

public interface RowService {
    RowResponseDto create(RowRequestDto dto);

    RowResponseDto update(Long id, RowUpdateDto dto);

    void delete(Long id);

    RowResponseDto getById(Long id);

    List<RowResponseDto> getAll();
}
