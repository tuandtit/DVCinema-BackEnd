package com.cinema.booking_app.room.dto.request.create;

import jakarta.validation.constraints.Min;
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
public class RoomRequestDto {
    @NotBlank(message = "Không được bỏ trống tên phòng chiếu")
    String name;

    @NotNull(message = "Phòng chiếu phải thuộc 1 rạp chiếu")
    Long cinemaId;

    @NotNull(message = "Số hàng trong phòng chiếu không được để trống")
    @Min(value = 1, message = "Số hàng ít nhất là 1")
    Integer totalRows;

    @NotNull(message = "Số ghế trong hàng không được để trống")
    @Min(value = 1, message = "Số ghế ít nhất là 1")
    Integer seatsPerRow;
}
