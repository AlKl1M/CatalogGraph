package com.alkl1m.cataloguegraph.service.impl;

import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.exception.ProductNotFoundException;
import com.alkl1m.cataloguegraph.exception.ReviewNotFoundException;
import com.alkl1m.cataloguegraph.repository.ReviewRepository;
import com.alkl1m.cataloguegraph.service.ProductService;
import com.alkl1m.cataloguegraph.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис для управления отзывами.
 * Предоставляет методы для получения, добавления, обновления и удаления отзывов,
 * а также для работы с продуктами, связанными с отзывами.
 *
 * @author AlKl1M
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProductService productService;
    private final ReviewRepository reviewRepository;

    /**
     * Получает список отзывов для указанного продукта с пагинацией.
     *
     * @param productId уникальный идентификатор продукта
     * @param page      номер страницы для пагинации
     * @param size      количество элементов на странице
     * @return список отзывов, связанных с указанным продуктом
     */
    @Override
    public Flux<Review> getReviewsByProductId(String productId, int page, int size) {
        return reviewRepository.findByProductId(productId)
                .skip((long) page * size)
                .take(size);
    }

    /**
     * Получает отзыв по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор отзыва
     * @return отзыв с указанным идентификатором
     */
    @Override
    public Mono<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    /**
     * Добавляет новый отзыв и ассоциирует его с продуктом.
     *
     * @param review объект отзыва для добавления
     * @return добавленный отзыв
     */
    @Override
    public Mono<Review> addReview(Review review) {
        return reviewRepository.save(review)
                .flatMap(savedReview ->
                        productService.addReviewToProduct(review.getProductId(), savedReview)
                                .thenReturn(savedReview)
                );
    }

    /**
     * Обновляет информацию о существующем отзыве.
     *
     * @param id      уникальный идентификатор отзыва
     * @param rating  новый рейтинг отзыва
     * @param comment новый комментарий отзыва
     * @return обновленный отзыв
     */
    @Override
    public Mono<Review> updateReview(String id, Integer rating, String comment) {
        return reviewRepository.findById(id)
                .flatMap(existingReview -> {
                    existingReview.setRating(rating);
                    existingReview.setComment(comment);
                    return reviewRepository.save(existingReview);
                })
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found")));
    }

    /**
     * Удаляет отзыв по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор отзыва
     * @return результат операции удаления (true, если удаление прошло успешно)
     */
    @Override
    public Mono<Boolean> deleteReview(String id) {
        return reviewRepository.existsById(id)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found")))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return reviewRepository.deleteById(id)
                                .then(Mono.just(true));
                    } else {
                        return Mono.just(false);
                    }
                });
    }

}
