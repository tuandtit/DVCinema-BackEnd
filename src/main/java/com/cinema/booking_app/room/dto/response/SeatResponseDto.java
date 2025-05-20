package com.cinema.booking_app.room.dto.response;

import com.cinema.booking_app.common.enums.SeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatResponseDto {
    Long id;
    String seatNumber;
    SeatType seatType;
    boolean isHeld;
    boolean isBooked;
    boolean selected;
    Long selectedByUserId;
}
