package com.cinema.booking_app.common.error;

import com.cinema.booking_app.common.constant.AppConstant;
import com.cinema.booking_app.config.languages.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionTranslator {

    private final Translator translator;

    private ResponseEntity<ErrorResponse> badRequest(ErrorResponse result) {
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> internalServerError(ErrorResponse result) {
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> notFound(ErrorResponse result) {
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> forbidden(ErrorResponse result) {
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ErrorResponse> unauthorized(ErrorResponse result) {
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("service", ex.getMessage());
        final var status = HttpStatus.valueOf(Integer.parseInt(ex.getErrorCode()));
        return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode(), status.getReasonPhrase(), map), status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("service", translator.toLocale("error.access.denied"));
        return forbidden(
                new ErrorResponse(AppConstant.FORBIDDEN.getCode(), AppConstant.SERVICE_ERROR.getMessage(), map)
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("service", translator.toLocale("error.login.bad_credentials"));
        return unauthorized(
                new ErrorResponse(AppConstant.UNAUTHORIZED.getCode(), AppConstant.UNAUTHORIZED.getMessage(), map)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        return badRequest(
                new ErrorResponse(AppConstant.BAD_REQUEST.getCode(), AppConstant.BAD_REQUEST.getMessage(), errors)
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("duplicate", translator.toLocale("Tên đã tồn tại"));

        return badRequest(
                new ErrorResponse(AppConstant.BAD_REQUEST.getCode(), AppConstant.BAD_REQUEST.getMessage(), error)
        );
    }
}
