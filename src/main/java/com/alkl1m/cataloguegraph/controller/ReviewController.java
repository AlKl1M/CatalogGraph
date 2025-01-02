package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @QueryMapping
    public List<Review> getReviews(String productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @QueryMapping
    public Review getReviewById(String id) {
        return reviewService.getReviewById(id).orElse(null);
    }

    @MutationMapping
    public Review addReview(Review review) {
        return reviewService.addReview(review);
    }

    @MutationMapping
    public Review updateReview(String id, Review review) {
        return reviewService.updateReview(id, review);
    }

    @MutationMapping
    public Boolean deleteReview(String id) {
        return reviewService.deleteReview(id);
    }

}
