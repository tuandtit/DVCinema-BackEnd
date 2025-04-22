package com.cinema.booking_app.room.dto.request.update;

import com.cinema.booking_app.common.enums.SeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatUpdateDto {
    String seatNumber;
    SeatType seatType;
    Long rowId;
}
