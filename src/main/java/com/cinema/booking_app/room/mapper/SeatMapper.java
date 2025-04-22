package com.cinema.booking_app.room.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.room.dto.request.create.SeatRequestDto;
import com.cinema.booking_app.room.dto.response.SeatResponseDto;
import com.cinema.booking_app.room.entity.SeatEntity;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface SeatMapper extends EntityMapper<SeatEntity, SeatRequestDto, SeatResponseDto> {
}
