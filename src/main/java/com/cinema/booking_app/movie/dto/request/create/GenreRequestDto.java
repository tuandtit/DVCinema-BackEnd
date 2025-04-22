package com.cinema.booking_app.movie.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreRequestDto {
    @NotBlank(message = "Tên thể loại không được để trống")
    String name;
}
