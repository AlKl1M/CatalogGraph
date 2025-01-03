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

    private final ProductService productService;
    private final ReviewRepository reviewRepository;

    public Flux<Review> getReviewsByProductId(String productId, int page, int size) {
        return reviewRepository.findByProductId(productId)
                .skip((long) page * size)
                .take(size);
    }

    public Mono<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    public Mono<Review> addReview(Review review) {
        return reviewRepository.save(review)
                .flatMap(savedReview ->
                        productService.addReviewToProduct(review.getProductId(), savedReview)
                                .thenReturn(savedReview)
                );
    }

    public Mono<Review> updateReview(String id, Integer rating, String comment) {
        return reviewRepository.findById(id)
                .flatMap(existingReview -> {
                    existingReview.setRating(rating);
                    existingReview.setComment(comment);
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