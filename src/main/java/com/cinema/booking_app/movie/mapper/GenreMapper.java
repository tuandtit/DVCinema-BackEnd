package com.cinema.booking_app.movie.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.movie.dto.request.create.GenreRequestDto;
import com.cinema.booking_app.movie.dto.response.GenreResponseDto;
import com.cinema.booking_app.movie.entity.GenreEntity;
import org.mapstruct.Mapper;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface GenreMapper extends EntityMapper<GenreEntity, GenreRequestDto, GenreResponseDto> {
}
