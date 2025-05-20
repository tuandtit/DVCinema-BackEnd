package com.cinema.booking_app.room.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDto {
    Long seatId;
    String seatName;
    Boolean isBooked;
    Boolean isHeld;
    Boolean selected;
    Long selectedByUserId;
}
