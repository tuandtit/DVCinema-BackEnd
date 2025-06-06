package com.cinema.booking_app.booking.dto.response;

import com.cinema.booking_app.common.enums.SeatStatus;
import com.cinema.booking_app.common.enums.SeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDto {
    Long seatShowtimeId;
    Long seatId;
    String seatName;
    SeatStatus status;
    Long selectedByUserId;
    SeatType seatType;
    BigDecimal price;
}
