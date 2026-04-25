package com.dotohtwo.review_processor.model;

public record UserFollowedEvent(String followerId, String followedUserId) {}
