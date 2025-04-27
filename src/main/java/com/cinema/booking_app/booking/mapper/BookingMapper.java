package com.cinema.booking_app.booking.mapper;

import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.booking.entity.TicketEntity;
import com.cinema.booking_app.common.base.mapper.DefaultConfigMapper;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper(
        config = DefaultConfigMapper.class
)
public interface BookingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "bookingCode", target = "bookingCode")
    @Mapping(source = "showtime.id", target = "showtimeId")
    @Mapping(source = "showtime.movie.title", target = "movieTitle")
    @Mapping(source = "showtime.room.name", target = "roomName")
    @Mapping(source = "showtime", target = "showDateTime", qualifiedByName = "mapShowDateTime")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "tickets", target = "seats", qualifiedByName = "mapSeats")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "createdDate", target = "createdDate")
    BookingResponseDto toDto(BookingEntity entity);

    List<BookingResponseDto> toDtoList(List<BookingEntity> entities);

    @Named("mapShowDateTime")
    default LocalDateTime mapShowDateTime(ShowtimeEntity showtime) {
        return LocalDateTime.of(showtime.getShowDate(), showtime.getStartTime());
    }

    @Named("mapSeats")
    default List<String> mapSeats(Set<TicketEntity> tickets) {
        return tickets.stream()
                .map(ticket -> ticket.getSeat().getSeatNumber())
                .toList();
    }
}
