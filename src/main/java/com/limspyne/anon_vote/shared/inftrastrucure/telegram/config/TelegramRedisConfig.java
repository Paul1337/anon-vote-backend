package com.limspyne.anon_vote.shared.inftrastrucure.telegram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limspyne.anon_vote.shared.application.telegram.dto.UserTelegramSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class TelegramRedisConfig {
    @Bean
    public RedisTemplate<String, UserTelegramSession> userTgSessionRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, UserTelegramSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<UserTelegramSession> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, UserTelegramSession.class);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
