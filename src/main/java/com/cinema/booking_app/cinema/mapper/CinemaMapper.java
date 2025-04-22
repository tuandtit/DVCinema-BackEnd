package com.cinema.booking_app.cinema.mapper;

import com.cinema.booking_app.cinema.dto.request.CinemaRequestDto;
import com.cinema.booking_app.cinema.dto.response.CinemaResponseDto;
import com.cinema.booking_app.cinema.entity.CinemaEntity;
import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface CinemaMapper extends EntityMapper<CinemaEntity, CinemaRequestDto, CinemaResponseDto> {
}
