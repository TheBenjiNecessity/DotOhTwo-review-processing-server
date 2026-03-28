package com.dotohtwo.review_processor.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class FollowerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FollowerServiceClient.class);

    private final WebClient webClient;

    public FollowerServiceClient(@Value("${follower-service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Returns the IDs of all users who follow the given userId.
     * Calls GET /users/{userId}/followers → String[]
     */
    public Flux<String> getFollowers(String userId) {
        return webClient.get()
                .uri("/users/{userId}/followers", userId)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(error -> logger.error("Failed to fetch followers for user {}: {}", userId, error.getMessage()));
    }
}
