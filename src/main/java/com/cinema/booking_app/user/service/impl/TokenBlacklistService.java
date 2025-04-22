package com.cinema.booking_app.user.service.impl;

import com.cinema.booking_app.user.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;

    /**
     * Đánh dấu accessToken vào blacklist với TTL tương ứng thời gian hết hạn.
     */
    public void blacklistAccessToken(String accessToken) {
        long ttlInSeconds = tokenProvider.getRemainingTTL(accessToken); // Tính TTL dựa trên `exp` của token

        if (ttlInSeconds > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + accessToken,
                "true",
                Duration.ofSeconds(ttlInSeconds)
            );
        }
    }

    /**
     * Kiểm tra accessToken có đang nằm trong blacklist không.
     */
    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken)
        );
    }
}
