package com.dotohtwo.review_processor.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TimelineRepository {

    private static final String TIMELINE_PREFIX = "timeline:";
    private static final String AUTHORED_PREFIX = "authored:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public TimelineRepository(@Qualifier("reactiveStringRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Long> addToTimeline(String userId, String reviewId) {
        return redisTemplate.opsForList().leftPush(TIMELINE_PREFIX + userId, reviewId);
    }

    public Mono<Long> addToAuthoredReviews(String authorId, String reviewId) {
        return redisTemplate.opsForList().leftPush(AUTHORED_PREFIX + authorId, reviewId);
    }

    public Flux<String> getAuthoredReviewIds(String authorId) {
        return redisTemplate.opsForList().range(AUTHORED_PREFIX + authorId, 0, -1);
    }
}
