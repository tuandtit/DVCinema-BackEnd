package com.cinema.booking_app.user.service.impl;

import com.cinema.booking_app.common.enums.AccountStatus;
import com.cinema.booking_app.common.enums.AuthProvider;
import com.cinema.booking_app.common.enums.ERole;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.config.languages.Translator;
import com.cinema.booking_app.user.dto.request.SignInGoogleRequest;
import com.cinema.booking_app.user.dto.request.SignInRequest;
import com.cinema.booking_app.user.dto.request.SignUpRequest;
import com.cinema.booking_app.user.dto.response.AccountDto;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.entity.RefreshTokenEntity;
import com.cinema.booking_app.user.entity.RoleEntity;
import com.cinema.booking_app.user.mapper.AccountMapper;
import com.cinema.booking_app.user.repository.AccountRepository;
import com.cinema.booking_app.user.repository.RefreshTokenRepository;
import com.cinema.booking_app.user.repository.RoleRepository;
import com.cinema.booking_app.user.security.jwt.TokenProvider;
import com.cinema.booking_app.user.service.AuthenticationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.cinema.booking_app.common.constant.AppConstant.PASSWORD_DEFAULT;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    //Other
    PasswordEncoder passwordEncoder;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenProvider tokenProvider;
    Translator translator;

    //Repository
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    RefreshTokenRepository refreshTokenRepository;

    //Mapper
    AccountMapper accountMapper;

    @Override
    public AccountDto signUp(SignUpRequest request) {
        if (accountRepository.existsByUsername(request.username())) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()), translator.toLocale("error.username.exists"));
        }
        final var entity = accountMapper.toEntity(request);
        RoleEntity role = roleRepository.findByName(ERole.USER).orElse(new RoleEntity(ERole.USER));
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.addRole(role);
        entity.setStatus(AccountStatus.ACTIVE);
        return accountMapper.toDto(accountRepository.save(entity));
    }

    @Override
    public AccountDto signIn(SignInRequest request, HttpServletResponse response) {
        final var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                request.username().trim(),
                request.password().trim()
        );

        final var authentication = authenticationManagerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final var jwtAccessToken = tokenProvider.createToken(authentication);
        final var jwtRefreshToken = tokenProvider.createRefreshToken(authentication);

        final var username = tokenProvider.getUserName(jwtAccessToken);
        final var account = accountRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("[SignInService:signIn] Account :{} not found", username);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "ACCOUNT NOT FOUND");
                });

        if (account.getStatus().equals(AccountStatus.INACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, translator.toLocale("error.account.locked"));
        }
        updateRevokedRefreshToken(account);

        //Let's save the refreshToken as well
        saveRefreshToken(account, jwtRefreshToken);
        //Creating the cookie
        createRefreshTokenCookie(response, jwtRefreshToken);
        AccountDto dto = accountMapper.toDto(jwtAccessToken, jwtRefreshToken);
        dto.setDisplayName(account.getDisplayName());
        dto.setUserId(account.getId());
        return dto;
    }

    @Override
    public AccountDto signInGoogle(SignInGoogleRequest request, HttpServletResponse response) {
        try {
            FirebaseAuth.getInstance().verifyIdToken(request.getGoogleToken());
            final var email = request.getEmail();
            final var account = accountRepository.findByUsernameAndAuthProvider(email, AuthProvider.GOOGLE)
                    .orElseGet(() -> createAccount(email, request.getAvatar()));

            final var authentication = tokenProvider.getAuthentication(account);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final var jwtAccessToken = tokenProvider.createToken(authentication);
            final var jwtRefreshToken = tokenProvider.createRefreshToken(authentication);

            updateRevokedRefreshToken(account);

            saveRefreshToken(account, jwtRefreshToken);

            createRefreshTokenCookie(response, jwtRefreshToken);

            return accountMapper.toDto(jwtAccessToken, jwtRefreshToken);

        } catch (FirebaseAuthException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @Override
    public AccountDto accessTokenByRefreshToken(HttpServletRequest req) {
        final var refreshToken = tokenProvider.resolveToken(req);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token");
        }
        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.
        var refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(tokens -> !tokens.isRevoked())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked"));

        final var account = refreshTokenEntity.getAccount();

        updateRevokedRefreshToken(account);

        //Now create the Authentication object
        final var authentication = tokenProvider.getAuthentication(account);

        //Use the authentication object to generate new jwt as the Authentication object that we will have may not contain correct role.
        final var jwtAccessToken = tokenProvider.createToken(authentication);

        return accountMapper.toDto(jwtAccessToken);
    }

    private AccountEntity createAccount(String email, String avatar) {
        final var account = new AccountEntity();
        account.setUsername(email);
        account.setAvatar(avatar);
        account.setPasswordHash(passwordEncoder.encode(PASSWORD_DEFAULT));
        account.setAuthProvider(AuthProvider.GOOGLE);
        account.setRoles(List.of(roleRepository.findByName(ERole.USER).orElse(new RoleEntity(ERole.USER))));
        return accountRepository.save(account);
    }

    private void updateRevokedRefreshToken(AccountEntity account) {
        final var refreshTokenEntities = refreshTokenRepository.findByAccountAndRevoked(account, false);
        refreshTokenEntities.forEach(refreshTokenEntity -> refreshTokenEntity.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokenEntities);
    }

    private void createRefreshTokenCookie(final HttpServletResponse servletResponse, final Jwt jwt) {
        final var refreshTokenCookie = new Cookie("refresh_token", jwt.getTokenValue());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60); // 15 day to seconds
        servletResponse.addCookie(refreshTokenCookie);
    }

    private void saveRefreshToken(AccountEntity account, Jwt jwt) {
        final var refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setRefreshToken(jwt.getTokenValue());
        refreshTokenEntity.setAccount(account);
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
