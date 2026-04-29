package com.dotohtwo.review_processor.service;

import com.dotohtwo.review_processor.client.FollowerServiceClient;
import com.dotohtwo.models.dto.ReviewCreatedEvent;
import com.dotohtwo.review_processor.repository.ReviewRepository;
import com.dotohtwo.review_processor.repository.TimelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TimelineService {

    private static final Logger logger = LoggerFactory.getLogger(TimelineService.class);

    private final FollowerServiceClient followerServiceClient;
    private final TimelineRepository timelineRepository;
    private final ReviewRepository reviewRepository;

    @Value("${timeline.follow-backfill-days:30}")
    private int backfillDays;

    public TimelineService(FollowerServiceClient followerServiceClient,
                           TimelineRepository timelineRepository,
                           ReviewRepository reviewRepository) {
        this.followerServiceClient = followerServiceClient;
        this.timelineRepository = timelineRepository;
        this.reviewRepository = reviewRepository;
    }

    // Fan-out on write: indexes the review under the author and pushes it to every follower's timeline in parallel.
    public Mono<Void> fanOutToFollowers(ReviewCreatedEvent review) {
        Mono<Void> indexUpdate = timelineRepository
                .addToAuthoredReviews(review.authorId(), review.reviewId().toString())
                .then();

        Mono<Void> fanOut = followerServiceClient.getFollowers(review.authorId())
                .flatMap(followerId ->
                        timelineRepository.addToTimeline(followerId, review.reviewId().toString())
                                .doOnSuccess(ignored -> logger.info("Added review {} to timeline of user {}", review.reviewId(), followerId))
                                .doOnError(error -> logger.error("Failed to add review {} to timeline of user {}: {}", review.reviewId(), followerId, error.getMessage()))
                )
                .then();

        return Mono.when(indexUpdate, fanOut);
    }

    // Called on a new follow: retroactively adds the followed user's recent reviews (within backfillDays) to the follower's timeline.
    public Mono<Void> backfillTimelineForFollower(String followerId, String followedUserId) {
        Instant cutoff = Instant.now().minus(backfillDays, ChronoUnit.DAYS);

        return timelineRepository.getAuthoredReviewIds(followedUserId)
                .flatMap(reviewId -> reviewRepository.findById(reviewId)
                        .filter(review -> review.createdAt().isAfter(cutoff))
                        .flatMap(review ->
                                timelineRepository.addToTimeline(followerId, review.reviewId().toString())
                                        .doOnSuccess(ignored -> logger.info("Backfilled review {} to timeline of user {} (followed {})",
                                                review.reviewId(), followerId, followedUserId))
                                        .doOnError(error -> logger.error("Failed to backfill review {} to timeline of user {}: {}",
                                                review.reviewId(), followerId, error.getMessage()))
                        )
                )
                .then();
    }
}
