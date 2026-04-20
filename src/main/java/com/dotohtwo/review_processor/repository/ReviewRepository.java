package com.dotohtwo.review_processor.repository;

import com.dotohtwo.models.dto.ReviewCreatedEvent;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReviewRepository {

    private static final String KEY_PREFIX = "review:";

    private final ReactiveRedisTemplate<String, ReviewCreatedEvent> redisTemplate;

    public ReviewRepository(ReactiveRedisTemplate<String, ReviewCreatedEvent> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> save(ReviewCreatedEvent review) {
        return redisTemplate.opsForValue().set(KEY_PREFIX + review.reviewId(), review);
    }

    public Mono<ReviewCreatedEvent> findById(String id) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + id);
    }
}
