package com.cinema.booking_app.user.service;

import com.cinema.booking_app.user.dto.response.AccountDto;
import com.cinema.booking_app.user.dto.request.SignInGoogleRequest;
import com.cinema.booking_app.user.dto.request.SignInRequest;
import com.cinema.booking_app.user.dto.request.SignUpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthenticationService {
    AccountDto signUp(SignUpRequest request);

    AccountDto signIn(SignInRequest request, HttpServletResponse response);

    AccountDto accessTokenByRefreshToken(HttpServletRequest req);

    AccountDto signInGoogle(SignInGoogleRequest request, HttpServletResponse response);
}
