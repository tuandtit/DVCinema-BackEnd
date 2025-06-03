package com.cinema.booking_app.user.controller;

import com.cinema.booking_app.user.dto.UserInfoDto;

public interface UserApi {
    UserInfoDto getUserInfo(Long userId);

    Long updateUserInfo(UserInfoDto userInfoDto);
}
