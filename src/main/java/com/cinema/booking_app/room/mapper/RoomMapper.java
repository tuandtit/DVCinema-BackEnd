package com.cinema.booking_app.room.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.room.dto.request.create.RoomRequestDto;
import com.cinema.booking_app.room.dto.response.RoomResponseDto;
import com.cinema.booking_app.room.entity.RoomEntity;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class,
        uses = {RowMapper.class}
)
public interface RoomMapper extends EntityMapper<RoomEntity, RoomRequestDto, RoomResponseDto> {
}
