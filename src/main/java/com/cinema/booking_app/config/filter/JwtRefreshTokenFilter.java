package com.cinema.booking_app.config.filter;

import com.cinema.booking_app.config.properties.RsaKeyProperties;
import com.cinema.booking_app.user.security.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshTokenFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final RsaKeyProperties rsaKeyProperties;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("[JwtRefreshTokenFilter:doFilterInternal] :: Started ");

            log.info("[JwtRefreshTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            final var jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyProperties.rsaPublicKey()).build();

            final var token = tokenProvider.resolveToken(request);

            if (ObjectUtils.isEmpty(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            final var jwtRefreshToken = jwtDecoder.decode(token);

            final var userName = tokenProvider.getUserName(jwtRefreshToken);

            if (!userName.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Check if refreshToken isPresent in database and is valid
                final var isRefreshTokenValidInDatabase = tokenProvider.isRefreshTokenValidInDatabase(jwtRefreshToken);

                UserDetails userDetails = tokenProvider.userDetails(userName);
                if (tokenProvider.isTokenValid(jwtRefreshToken, userDetails) && isRefreshTokenValidInDatabase) {
                    final var securityContext = SecurityContextHolder.createEmptyContext();

                    final var createdToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    createdToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(createdToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            log.info("[JwtRefreshTokenFilter:doFilterInternal] Completed");
            filterChain.doFilter(request, response);
        } catch (JwtValidationException jwtValidationException) {
            log.error("[JwtRefreshTokenFilter:doFilterInternal] Exception due to :{}", jwtValidationException.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, jwtValidationException.getMessage());
        }
    }
}
