package com.cinema.booking_app.cinema.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CityRequestDto {

    @NotBlank(message = "Tên thành phố không được để trống")
    String name;
}
