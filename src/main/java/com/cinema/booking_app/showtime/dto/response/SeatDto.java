package com.cinema.booking_app.showtime.dto.response;

import com.cinema.booking_app.common.enums.SeatStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
}
