package com.cinema.booking_app.config.filter;

import com.cinema.booking_app.user.security.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain filterChain)
            throws ServletException, IOException {

        final String token = tokenProvider.resolveToken(request);

        // Nếu không có token thì cho qua
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Nếu token hợp lệ -> thiết lập Authentication
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[JwtAccessTokenFilter:doFilterInternal] Authentication set to SecurityContextHolder : {}", authentication.getName());
        }

        filterChain.doFilter(request, response);
    }

}
