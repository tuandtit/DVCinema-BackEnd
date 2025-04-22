package com.cinema.booking_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String PREFIX_AUTHORITIES = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String roles = jwt.getClaim("scope"); // Lấy "ROLE_ADMIN,ROLE_USER"

        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(String::trim) // Xóa khoảng trắng thừa
                .map(auth -> auth.startsWith(PREFIX_AUTHORITIES) ? auth : PREFIX_AUTHORITIES + auth)
                .map(SimpleGrantedAuthority::new) // Chuyển thành GrantedAuthority
                .toList();

        log.info("Converted Authorities: {}", authorities); // Log để debug
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
