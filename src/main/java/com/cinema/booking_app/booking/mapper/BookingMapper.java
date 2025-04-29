package com.cinema.booking_app.booking.mapper;

import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        config = DefaultConfigMapper.class,
        uses = {TicketMapper.class}
)
public interface BookingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookingCode", target = "bookingCode")
    @Mapping(source = "showtime.movie.title", target = "movieTitle")
    @Mapping(source = "showtime.room.name", target = "roomName")
    @Mapping(source = "showtime.showDate", target = "showDate")
    @Mapping(source = "showtime.startTime", target = "showTime")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "tickets", target = "tickets")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "createdDate", target = "createdDate")
    BookingResponseDto toDto(BookingEntity entity);

    List<BookingResponseDto> toDtoList(List<BookingEntity> entities);
}
