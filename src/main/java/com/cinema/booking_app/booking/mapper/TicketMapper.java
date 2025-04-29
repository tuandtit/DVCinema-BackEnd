package com.cinema.booking_app.booking.mapper;

import com.cinema.booking_app.booking.dto.response.TicketResponseDto;
import com.cinema.booking_app.booking.entity.TicketEntity;
import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.room.entity.SeatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface TicketMapper {
    @Mapping(source = "price", target = "price")
    @Mapping(source = "seat", target = "seatName", qualifiedByName = "mapSeat")
    TicketResponseDto toDto(TicketEntity ticketEntity);

    @Named("mapSeat")
    default String mapSeat(SeatEntity entity) {
        return entity.getRow().getLabel() + entity.getSeatNumber();
    }
}
