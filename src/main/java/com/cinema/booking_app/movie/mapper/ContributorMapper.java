package com.cinema.booking_app.movie.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.movie.dto.request.create.ContributorCreateRequestDto;
import com.cinema.booking_app.movie.dto.request.update.ContributorUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.ContributorResponseDto;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import org.mapstruct.*;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface ContributorMapper extends EntityMapper<ContributorEntity, ContributorCreateRequestDto, ContributorResponseDto> {
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    void update(ContributorUpdateRequestDto dto, @MappingTarget ContributorEntity entity);
}
