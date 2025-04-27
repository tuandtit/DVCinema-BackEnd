package com.cinema.booking_app.showtime.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeResponseDto {
    Long id;
    Long movieId;
    String movieTitle;
    Long roomId;
    String roomName;
    Long cinemaId;
    String cinemaName;
    LocalDate showDate;
    LocalTime startTime;
    BigDecimal ticketPrice;
}