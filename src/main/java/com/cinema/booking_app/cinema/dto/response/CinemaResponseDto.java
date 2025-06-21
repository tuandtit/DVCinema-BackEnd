package com.cinema.booking_app.cinema.dto.response;

import com.cinema.booking_app.room.dto.response.RoomResponseDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaResponseDto {
    Long id;
    String name;
    String address;
    List<RoomResponseDto> rooms;
}
