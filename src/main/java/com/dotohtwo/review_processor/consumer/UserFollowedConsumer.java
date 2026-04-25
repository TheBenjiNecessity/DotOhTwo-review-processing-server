package com.dotohtwo.review_processor.consumer;

import com.dotohtwo.review_processor.model.UserFollowedEvent;
import com.dotohtwo.review_processor.service.TimelineService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserFollowedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowedConsumer.class);

    private final TimelineService timelineService;

    public UserFollowedConsumer(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    @KafkaListener(
            topics = "${kafka.topic.users-followed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "userFollowedKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, UserFollowedEvent> record) {
        if (record.value() == null) {
            logger.error("Skipping undeserializable follow event at partition={} offset={}",
                    record.partition(), record.offset());
            return;
        }
        UserFollowedEvent event = record.value();
        logger.info("User {} followed user {}", event.followerId(), event.followedUserId());

        timelineService.backfillTimelineForFollower(event.followerId(), event.followedUserId())
                .subscribe(
                        null,
                        error -> logger.error("Failed to backfill timeline for follower {} from {}: {}",
                                event.followerId(), event.followedUserId(), error.getMessage())
                );
    }
}
