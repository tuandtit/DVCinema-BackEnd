package com.cinema.booking_app.room.dto.request.create;

import com.cinema.booking_app.common.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatRequestDto {
    @NotBlank(message = "Không được bỏ trống số ghế")
    String seatNumber;

    @NotNull(message = "Không được bỏ trống loại ghế")
    SeatType seatType;

    @NotNull(message = "Ghế xem phim phải thuộc 1 hàng ghế")
    Long rowId;

    Boolean isBooked = false;

    Boolean isHeld = false;

    List<Long> seatIds;
}
