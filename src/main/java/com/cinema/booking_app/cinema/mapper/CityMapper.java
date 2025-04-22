package com.cinema.booking_app.cinema.mapper;

import com.cinema.booking_app.cinema.dto.request.CityRequestDto;
import com.cinema.booking_app.cinema.dto.response.CityResponseDto;
import com.cinema.booking_app.cinema.entity.CityEntity;
import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class,
        uses = {CinemaMapper.class}
)
public interface CityMapper extends EntityMapper<CityEntity, CityRequestDto, CityResponseDto> {
}
