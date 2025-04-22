package com.cinema.booking_app.movie.dto.request.create;

import com.cinema.booking_app.common.enums.Gender;
import com.cinema.booking_app.common.enums.ContributorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ContributorCreateRequestDto {
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String name;

    @Size(max = 100, message = "Tên gọi khác không được vượt quá 100 ký tự")
    private String stageName;

    @Size(max = 1000, message = "Giới thiệu không được vượt quá 1000 ký tự")
    private String introduction;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
    private LocalDate birthDate;

    @Size(max = 255, message = "Đường dẫn ảnh không được vượt quá 255 ký tự")
    private String photoUrl;

    @NotNull(message = "Loại  không được để trống")
    private ContributorType contributorType;
}