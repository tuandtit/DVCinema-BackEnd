package com.cinema.booking_app.user.controller.impl;

import com.cinema.booking_app.user.service.AuthenticationService;
import com.cinema.booking_app.user.dto.response.AccountDto;
import com.cinema.booking_app.user.dto.request.SignInGoogleRequest;
import com.cinema.booking_app.user.dto.request.SignInRequest;
import com.cinema.booking_app.user.dto.request.SignUpRequest;
import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.user.controller.AuthenticationResource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationResourceImpl implements AuthenticationResource {

    private final AuthenticationService authenticationService;
    private final LogoutHandler logoutHandler;

    @Override
    public Response<AccountDto> signUp(final SignUpRequest request) {
        return Response.ok(authenticationService.signUp(request));
    }

    @Override
    public Response<AccountDto> signIn(final SignInRequest request,final HttpServletResponse response) {
        return Response.ok(authenticationService.signIn(request, response));
    }

    @Override
    public Response<AccountDto> getAccessTokenByRefreshToken(final HttpServletRequest request) {
        return Response.ok(authenticationService.accessTokenByRefreshToken(request));
    }

    @Override
    public Response<AccountDto> signInByGoogle(final SignInGoogleRequest request,final HttpServletResponse response) {
        return Response.ok(authenticationService.signInGoogle(request, response));
    }

    @Override
    public Response<String> logout(HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return Response.ok("true");
    }
}
