package com.cinema.booking_app.user.security.jwt;

import com.cinema.booking_app.config.filter.JwtAccessTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JWTConfigure extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    @Override
    public void init(HttpSecurity builder) throws Exception {

    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.addFilterBefore(new JwtAccessTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
