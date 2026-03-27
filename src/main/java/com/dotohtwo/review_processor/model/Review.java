package com.dotohtwo.review_processor.model;

import java.time.Instant;

public record Review(
        String id,
        String productId,
        String author,
        int rating,
        String content,
        Instant createdAt
) {}
