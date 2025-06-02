package com.cinema.booking_app.user.service.impl;

import com.cinema.booking_app.user.repository.RefreshTokenRepository;
import com.cinema.booking_app.user.security.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogoutServiceImpl implements LogoutHandler {

    RefreshTokenRepository refreshTokenRepository;
    TokenProvider tokenProvider;

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        final String accessToken = tokenProvider.resolveToken(request);
        final String refreshToken = extractRefreshTokenFromCookies(request);

        if (ObjectUtils.isEmpty(accessToken) || ObjectUtils.isEmpty(refreshToken)) {
            return;
        }

        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(entity -> {
                    entity.setRevoked(true);
                    refreshTokenRepository.save(entity);
                });
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
