package com.cinema.booking_app.cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaRequestDto {
    @NotBlank(message = "Tên không được bỏ trống")
    String name;
    @NotNull(message = "Rạp phim phải thuộc 1 thành phố")
    Long cityId;
    String address;
}
