package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Flux<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public Mono<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    public Mono<Review> addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Mono<Review> updateReview(String id, Review updatedReview) {
        return reviewRepository.findById(id)
                .flatMap(existingReview -> {
                    existingReview.setRating(updatedReview.getRating());
                    existingReview.setComment(updatedReview.getComment());
                    return reviewRepository.save(existingReview);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found")));
    }

    public Mono<Boolean> deleteReview(String id) {
        return reviewRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return reviewRepository.deleteById(id)
                                .then(Mono.just(true));
                    } else {
                        return Mono.just(false);
                    }
                });
    }
}