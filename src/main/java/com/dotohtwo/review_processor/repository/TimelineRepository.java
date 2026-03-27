package com.dotohtwo.review_processor.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TimelineRepository {

    private static final String FOLLOWERS_PREFIX = "followers:";
    private static final String TIMELINE_PREFIX = "timeline:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public TimelineRepository(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Returns the IDs of all users who follow the given author.
     * Expected Redis key: followers:{authorId} (Set of userId strings)
     */
    public Flux<String> getFollowers(String authorId) {
        return redisTemplate.opsForSet().members(FOLLOWERS_PREFIX + authorId);
    }

    /**
     * Prepends a review ID to the front of the given user's timeline list.
     * Redis key: timeline:{userId} (List of review ID strings, newest first)
     */
    public Mono<Long> addToTimeline(String userId, String reviewId) {
        return redisTemplate.opsForList().leftPush(TIMELINE_PREFIX + userId, reviewId);
    }
}
