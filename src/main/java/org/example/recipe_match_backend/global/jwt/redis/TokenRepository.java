package org.example.recipe_match_backend.global.jwt.redis;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.global.jwt.JwtProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
// Redis 용 RefreshToken 저장소
public class TokenRepository {
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    private String keyOf(String userId) {
        return "refresh:" + userId;
    }

    // RefreshToken을 사용자 식별값을 키로 하여 저장
    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(keyOf(userId), refreshToken, Duration.ofDays(jwtProperties.getRefreshTokenExpiration()));     // 만료시간 설정 (7일)
    }

    // 키로 저장된 리프레시 토큰 조회
    public String findByKey(String userId) {
        return redisTemplate.opsForValue().get(keyOf(userId));
    }

    // 리프레시 토큰 삭제
    public void delete(String userId) {
        redisTemplate.delete(keyOf(userId));
    }
}
