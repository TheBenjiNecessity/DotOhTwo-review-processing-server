package com.dotohtwo.review_processor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "reviews")
@TestPropertySource(properties = {
		"spring.kafka.consumer.auto-offset-reset=earliest",
		"follower-service.base-url=http://localhost:9999"
})
class ReviewProcessorApplicationTests {

	@Test
	void contextLoads() {
	}

}
