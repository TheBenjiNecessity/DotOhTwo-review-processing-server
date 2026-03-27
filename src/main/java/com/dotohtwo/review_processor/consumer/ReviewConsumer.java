package com.dotohtwo.review_processor.consumer;

import com.dotohtwo.review_processor.model.Review;
import com.dotohtwo.review_processor.repository.ReviewRepository;
import com.dotohtwo.review_processor.service.TimelineService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReviewConsumer.class);

    private final ReviewRepository reviewRepository;
    private final TimelineService timelineService;

    public ReviewConsumer(ReviewRepository reviewRepository, TimelineService timelineService) {
        this.reviewRepository = reviewRepository;
        this.timelineService = timelineService;
    }

    @KafkaListener(topics = "${kafka.topic.reviews}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, Review> record) {
        Review review = record.value();
        logger.info("Received review — id: {}, productId: {}, author: {}, rating: {}, content: {}",
                review.id(), review.productId(), review.author(), review.rating(), review.content());

        reviewRepository.save(review)
                .flatMap(saved -> {
                    logger.info("Saved review {} to Redis", review.id());
                    return timelineService.fanOutToFollowers(review);
                })
                .subscribe(
                        null,
                        error -> logger.error("Failed to process review {}: {}", review.id(), error.getMessage())
                );
    }
}
