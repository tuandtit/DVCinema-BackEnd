package com.cinema.booking_app.room.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.room.dto.request.create.RowRequestDto;
import com.cinema.booking_app.room.dto.response.RowResponseDto;
import com.cinema.booking_app.room.entity.RowEntity;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class,
        uses = {SeatMapper.class}
)
public interface RowMapper extends EntityMapper<RowEntity, RowRequestDto, RowResponseDto> {
}
