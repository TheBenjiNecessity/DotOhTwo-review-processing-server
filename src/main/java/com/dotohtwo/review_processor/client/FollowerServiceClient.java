package com.dotohtwo.review_processor.client;

import com.dotohtwo.review_processor.config.ServiceTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class FollowerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FollowerServiceClient.class);

    private final WebClient webClient;

    public FollowerServiceClient(
            @Value("${follower-service.base-url}") String baseUrl,
            ServiceTokenProvider tokenProvider) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter((request, next) -> next.exchange(
                        ClientRequest.from(request)
                                .header("Authorization", "Bearer " + tokenProvider.getToken())
                                .build()
                ))
                .build();
    }

    /**
     * Returns the IDs of all users who follow the given userId.
     * Calls GET /users/{userId}/followers → String[]
     */
    public Flux<String> getFollowers(String username) {
        return webClient.get()
                .uri("/users/{username}/followers", username)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .flatMapMany(Flux::fromIterable)
                .doOnError(error -> logger.error("Failed to fetch followers for user {}: {}", username, error.getMessage()));
    }
}
