package com.cinema.booking_app.user.controller.impl;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.user.dto.UserInfoDto;
import com.cinema.booking_app.user.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Response<UserInfoDto> getUserInfo(@RequestParam Long userId) {
        return Response.ok(userService.getUserInfo(userId));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Long> updateUserInfo(@Valid UserInfoDto userInfoDto) {
        return Response.ok(userService.updateUserInfo(userInfoDto));
    }
}
