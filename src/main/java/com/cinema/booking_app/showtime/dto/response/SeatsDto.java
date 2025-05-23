package com.cinema.booking_app.showtime.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatsDto {
    Long rowId;
    List<SeatDto> seats;
}
