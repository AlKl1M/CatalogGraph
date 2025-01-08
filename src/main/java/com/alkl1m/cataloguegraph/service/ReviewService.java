package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    Flux<Review> getReviewsByProductId(String productId, int page, int size);

    Mono<Review> getReviewById(String id);

    Mono<Review> addReview(Review review);

    Mono<Review> updateReview(String id, Integer rating, String comment);

    Mono<Boolean> deleteReview(String id);

}
