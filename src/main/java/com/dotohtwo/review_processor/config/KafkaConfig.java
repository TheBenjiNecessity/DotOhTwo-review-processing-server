package com.dotohtwo.review_processor.config;

import com.dotohtwo.review_processor.model.UserFollowedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(buildErrorHandler());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserFollowedEvent> userFollowedKafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> baseConsumerFactory) {

        Map<String, Object> props = new HashMap<>(((DefaultKafkaConsumerFactory<?, ?>) baseConsumerFactory).getConfigurationProperties());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class.getName());
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, UserFollowedEvent.class.getName());
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.dotohtwo.review_processor.model");

        DefaultKafkaConsumerFactory<String, UserFollowedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new ErrorHandlingDeserializer<>());

        ConcurrentKafkaListenerContainerFactory<String, UserFollowedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(buildErrorHandler());
        return factory;
    }

    private DefaultErrorHandler buildErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    logger.error("Skipping unrecoverable record at topic={} partition={} offset={}: {}",
                            record.topic(), record.partition(), record.offset(), cause.getMessage());
                },
                new FixedBackOff(0L, 0L)
        );
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        errorHandler.setLogLevel(KafkaException.Level.DEBUG);
        return errorHandler;
    }
}
