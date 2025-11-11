package com.example.hybridchatserver.presence;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {

    private static final String PRESENCE_KEY_PREFIX = "user:";
    private static final String PRESENCE_KEY_SUFFIX = ":status";
    private final StringRedisTemplate redisTemplate;

    public PresenceService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setUserOnline(String username) {
        redisTemplate.opsForValue().set(PRESENCE_KEY_PREFIX + username + PRESENCE_KEY_SUFFIX, "online");
    }

    public void setUserOffline(String username) {
        redisTemplate.delete(PRESENCE_KEY_PREFIX + username + PRESENCE_KEY_SUFFIX);
    }

    public boolean isUserOnline(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PRESENCE_KEY_PREFIX + username + PRESENCE_KEY_SUFFIX));
    }
}
