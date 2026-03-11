package com.dotohtwo.review_processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReviewConsumer.class);

    private final ReviewRepository reviewRepository;

    public ReviewConsumer(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @KafkaListener(topics = "${kafka.topic.reviews}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, Review> record) {
        Review review = record.value();
        logger.info("Received review — id: {}, productId: {}, author: {}, rating: {}, content: {}",
                review.id(), review.productId(), review.author(), review.rating(), review.content());

        // TODO: Add feature to add this review to add each followers home timeline

        reviewRepository.save(review)
                .subscribe(
                        success -> logger.info("Saved review {} to Redis", review.id()),
                        error -> logger.error("Failed to save review {} to Redis: {}", review.id(), error.getMessage())
                );
    }
}
