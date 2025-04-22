package com.cinema.booking_app.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetAccessTokenByRefreshTokenRequest(@NotBlank String refreshToken) {
}
