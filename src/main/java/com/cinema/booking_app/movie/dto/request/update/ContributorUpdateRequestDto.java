package com.cinema.booking_app.movie.dto.request.update;

import com.cinema.booking_app.common.enums.ContributorType;
import com.cinema.booking_app.common.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContributorUpdateRequestDto {

    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    @Size(max = 100, message = "Tên gọi khác không được vượt quá 100 ký tự")
    private String stageName;

    @Size(max = 1000, message = "Giới thiệu không được vượt quá 1000 ký tự")
    private String introduction;

    private Gender gender; // Cho phép null vì có thể không update

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate birthDate;

    @Size(max = 255, message = "Đường dẫn ảnh không được vượt quá 255 ký tự")
    private String photoUrl;

    private ContributorType contributorType;
}
