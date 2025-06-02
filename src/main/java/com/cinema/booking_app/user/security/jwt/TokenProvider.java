package com.cinema.booking_app.user.security.jwt;

import com.cinema.booking_app.user.security.SecurityUtils;
import com.cinema.booking_app.config.properties.RsaKeyProperties;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.entity.RoleEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import com.cinema.booking_app.user.repository.RefreshTokenRepository;
import com.cinema.booking_app.common.error.BusinessException;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RsaKeyProperties rsaKeyProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    private static final String AUTHORITIES_KEY = "scope";
    private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";
    private static final String PREFIX_AUTHORIZE = "ROLE_";

    public Jwt createToken(Authentication authentication) {
        log.info("[TokenProvider:createToken] Token Creation Started for:{}", authentication.getName());

        final var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        final var now = Instant.now();

        final var claims = JwtClaimsSet.builder()
                .issuer("devtuna.com")
                .issuedAt(now)
                .expiresAt(now.plus(rsaKeyProperties.expired(), ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt createRefreshToken(Authentication authentication) {
        log.info("[TokenProvider:createRefreshToken] Token Creation Started for:{}", authentication.getName());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("devtuna.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, "REFRESH_TOKEN")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public boolean isRefreshTokenValidInDatabase(Jwt jwt) {
        return refreshTokenRepository.findByRefreshToken(jwt.getTokenValue())
                .map(token -> !token.isRevoked())
                .orElse(false);
    }

    public String resolveToken(final HttpServletRequest request) {
        final var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && SecurityUtils.isBearerToken(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(final String token) {
        try {
            JWTParser.parse(token);
            return getIfTokenIsExpired(jwtDecoder.decode(token));
        } catch (ParseException e) {
            log.error(INVALID_JWT_TOKEN, e);
            return false;
        }
    }

    public boolean isTokenValid(Jwt jwtToken, UserDetails userDetails) {
        final String userName = getUserName(jwtToken);
        boolean isTokenExpired = getIfTokenIsExpired(jwtToken);
        boolean isTokenUserSameAsDatabase = userName.equals(userDetails.getUsername());
        return !isTokenExpired && isTokenUserSameAsDatabase;

    }

    private boolean getIfTokenIsExpired(Jwt jwtToken) {
        return !Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }

    public long getRemainingTTL(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiration = jwt.getExpiresAt();
            Instant now = Instant.now();

            long ttlInSeconds = ChronoUnit.SECONDS.between(now, expiration);
            return ttlInSeconds > 0 ? ttlInSeconds : 0;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[TokenProvider:getRemainingTTL] Failed to decode token: {}", e.getMessage());
            return 0;
        }
    }

    public Authentication getAuthentication(final String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                            jwt.getClaimAsString(AUTHORITIES_KEY).split(","))
                    .filter(auth -> !auth.trim().isEmpty())
                    .map(auth -> auth.startsWith(PREFIX_AUTHORIZE) ? auth : PREFIX_AUTHORIZE + auth)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            User principal = new User(jwt.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } catch (JwtException e) {
            log.error("[TokenProvider:getAuthentication] Failed to decode token: {}", e.getMessage());
            throw new BusinessException(INVALID_JWT_TOKEN);
        }
    }

    public Authentication getAuthentication(final AccountEntity account) {
        // Extract user details from UserDetailsEntity
        String username = account.getUsername();
        String password = account.getPasswordHash();

        final var authorities = account.getRoles()
                .stream()
                .map(RoleEntity::getName)
                .map(Enum::name)
                .map(s -> s.startsWith(PREFIX_AUTHORIZE) ? s : PREFIX_AUTHORIZE + s)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    public String getUserName(final Jwt jwt) {
        return jwt.getSubject();
    }

    public UserDetails userDetails(final String username) {
        return accountRepository
                .findByUsername(username)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Account: %s does not exist".formatted(username)));
    }

    private User createSpringSecurityUser(AccountEntity account) {
        final var grantedAuthorities = account
                .getRoles()
                .stream()
                .map(RoleEntity::getName)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(account.getUsername(), account.getPasswordHash(), grantedAuthorities);
    }
}
