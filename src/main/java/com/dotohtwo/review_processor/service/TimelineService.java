package com.dotohtwo.review_processor.service;

import com.dotohtwo.review_processor.client.FollowerServiceClient;
import com.dotohtwo.review_processor.model.Review;
import com.dotohtwo.review_processor.repository.TimelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TimelineService {

    private static final Logger logger = LoggerFactory.getLogger(TimelineService.class);

    private final FollowerServiceClient followerServiceClient;
    private final TimelineRepository timelineRepository;

    public TimelineService(FollowerServiceClient followerServiceClient, TimelineRepository timelineRepository) {
        this.followerServiceClient = followerServiceClient;
        this.timelineRepository = timelineRepository;
    }

    /**
     * Pushes the review ID onto the timeline list of every user who follows the review's author.
     */
    public Mono<Void> fanOutToFollowers(Review review) {
        return followerServiceClient.getFollowers(review.author())
                .flatMap(followerId ->
                        timelineRepository.addToTimeline(followerId, review.id())
                                .doOnSuccess(ignored -> logger.info("Added review {} to timeline of user {}", review.id(), followerId))
                                .doOnError(error -> logger.error("Failed to add review {} to timeline of user {}: {}", review.id(), followerId, error.getMessage()))
                )
                .then();
    }
}
