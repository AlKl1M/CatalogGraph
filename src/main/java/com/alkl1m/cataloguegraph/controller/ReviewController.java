package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @QueryMapping
    public Flux<Review> getReviews(String productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @QueryMapping
    public Mono<Review> getReviewById(String id) {
        return reviewService.getReviewById(id);
    }

    @MutationMapping
    public Mono<Review> addReview(Review review) {
        return reviewService.addReview(review);
    }

    @MutationMapping
    public Mono<Review> updateReview(String id, Review review) {
        return reviewService.updateReview(id, review);
    }

    @MutationMapping
    public Mono<Boolean> deleteReview(String id) {
        return reviewService.deleteReview(id);
    }

}
