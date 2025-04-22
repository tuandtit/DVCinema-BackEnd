package com.cinema.booking_app.common.base.mapper;

import org.mapstruct.*;

import java.util.List;

public interface EntityMapper<E, Q, S> {
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    E toEntity(Q request);

//    @Named("toDto")
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
    S toDto(E entity);

    List<S> toDto(List<E> entities);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    List<E> toEntity(List<Q> requests);

    @Named(value = "update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void update(Q dto, @MappingTarget E entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateDto(@MappingTarget S dto, E entity);
}
