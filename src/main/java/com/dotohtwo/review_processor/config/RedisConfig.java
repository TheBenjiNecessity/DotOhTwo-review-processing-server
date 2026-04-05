package com.dotohtwo.review_processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.dotohtwo.review_processor.model.Review;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Review> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        RedisSerializer<Review> valueSerializer = new RedisSerializer<>() {
            @Override
            public byte[] serialize(Review review) throws SerializationException {
                try {
                    return objectMapper.writeValueAsBytes(review);
                } catch (Exception e) {
                    throw new SerializationException("Could not serialize Review", e);
                }
            }

            @Override
            public Review deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null) return null;
                try {
                    return objectMapper.readValue(bytes, Review.class);
                } catch (IOException e) {
                    throw new SerializationException("Could not deserialize Review", e);
                }
            }
        };

        RedisSerializationContext<String, Review> context = RedisSerializationContext
                .<String, Review>newSerializationContext(new StringRedisSerializer())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
