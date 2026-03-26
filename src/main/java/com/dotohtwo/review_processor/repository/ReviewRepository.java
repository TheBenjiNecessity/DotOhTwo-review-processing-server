package com.dotohtwo.review_processor;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReviewRepository {

    private static final String KEY_PREFIX = "review:";

    private final ReactiveRedisTemplate<String, Review> redisTemplate;

    public ReviewRepository(ReactiveRedisTemplate<String, Review> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> save(Review review) {
        return redisTemplate.opsForValue().set(KEY_PREFIX + review.id(), review);
    }

    public Mono<Review> findById(String id) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + id);
    }
}
