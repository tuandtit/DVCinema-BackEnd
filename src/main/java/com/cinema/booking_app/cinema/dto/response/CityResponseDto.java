package com.cinema.booking_app.cinema.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CityResponseDto {
    Long id;
    String name;
    List<CinemaResponseDto> cinemas;
}
