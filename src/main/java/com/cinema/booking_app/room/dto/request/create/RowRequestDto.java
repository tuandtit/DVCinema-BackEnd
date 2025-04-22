package com.cinema.booking_app.room.dto.request.create;

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
public class RowRequestDto {
    @NotBlank(message = "Không được bỏ trống nhãn hàng ghế")
    String label;

    @NotNull(message = "Hàng ghế xem phim phải thuộc 1 phòng chiếu")
    Long roomId;
}
