package com.cinema.booking_app.movie.mapper;

import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.common.base.mapper.EntityMapper;
import com.cinema.booking_app.movie.dto.request.create.MovieRequestDto;
import com.cinema.booking_app.movie.dto.request.update.MovieUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.MovieResponseDto;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import com.cinema.booking_app.movie.entity.GenreEntity;
import com.cinema.booking_app.movie.entity.MovieEntity;

import java.util.Collections;

import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        config = DefaultConfigMapper.class,
        uses = {GenreMapper.class, ContributorMapper.class}
)
public interface MovieMapper extends EntityMapper<MovieEntity, MovieRequestDto, MovieResponseDto> {
    @Named(value = "update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void update(MovieUpdateRequestDto dto, @MappingTarget MovieEntity entity);

    @Override
    @Mapping(target = "genreNames", expression = "java(mapGenreNames(entity))")
    @Mapping(target = "actorNames", expression = "java(mapActorNames(entity))")
    @Mapping(target = "directorName", expression = "java(mapDirectorName(entity))")
    MovieResponseDto toDto(MovieEntity entity);

    default Set<String> mapGenreNames(MovieEntity entity) {
        if (entity.getGenres() == null) return Collections.emptySet();
        return entity.getGenres()
                .stream()
                .map(GenreEntity::getName)
                .collect(Collectors.toSet());
    }

    default Set<String> mapActorNames(MovieEntity entity) {
        if (entity.getActors() == null) return Collections.emptySet();
        return entity.getActors()
                .stream()
                .map(ContributorEntity::getName)
                .collect(Collectors.toSet());
    }

    default String mapDirectorName(MovieEntity entity) {
        if (entity.getDirector() == null) return "";
        return entity.getDirector().getName();
    }
}
