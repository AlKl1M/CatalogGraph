package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @QueryMapping
    public Flux<Review> getReviews(
            @Argument String productId,
            @Argument Integer page,
            @Argument Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        return reviewService.getReviewsByProductId(productId, pageNumber, pageSize);
    }

    @QueryMapping
    public Mono<Review> getReviewById(@Argument String id) {
        return reviewService.getReviewById(id);
    }

    @MutationMapping
    public Mono<Review> addReview(
            @Argument String productId,
            @Argument String author,
            @Argument int rating,
            @Argument String comment
    ) {
        Review review = new Review();
        review.setProductId(productId);
        review.setAuthor(author);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        return reviewService.addReview(review);
    }

    @MutationMapping
    public Mono<Review> updateReview(
            @Argument String id,
            @Argument Integer rating,
            @Argument String comment
    ) {
        return reviewService.updateReview(id, rating, comment);
    }

    @MutationMapping
    public Mono<Boolean> deleteReview(@Argument String id) {
        return reviewService.deleteReview(id);
    }

}
