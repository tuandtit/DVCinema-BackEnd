package com.cinema.booking_app.room.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RowResponseDto {
    Long id;
    String label;
    List<SeatResponseDto> seats;
}
