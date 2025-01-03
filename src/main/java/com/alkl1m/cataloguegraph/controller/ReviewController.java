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

/**
 * Контроллер для управления отзывами продуктов.
 * Предоставляет методы для получения, добавления, обновления и удаления отзывов.
 *
 * @author AlKl1M
 */
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Получает список отзывов по идентификатору продукта с поддержкой пагинации.
     *
     * @param productId уникальный идентификатор продукта
     * @param page      номер страницы для пагинации (по умолчанию 0)
     * @param size      количество элементов на странице (по умолчанию 10)
     * @return список отзывов для указанного продукта
     */
    @QueryMapping
    public Flux<Review> getReviews(
            @Argument String productId,
            @Argument Integer page,
            @Argument Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        return reviewService.getReviewsByProductId(productId, pageNumber, pageSize);
    }

    /**
     * Получает отзыв по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор отзыва
     * @return отзыв с указанным идентификатором
     */
    @QueryMapping
    public Mono<Review> getReviewById(@Argument String id) {
        return reviewService.getReviewById(id);
    }

    /**
     * Добавляет новый отзыв для продукта.
     *
     * @param productId уникальный идентификатор продукта
     * @param author    автор отзыва
     * @param rating    рейтинг продукта (например, от 1 до 5)
     * @param comment   текст отзыва
     * @return добавленный отзыв
     */
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

    /**
     * Обновляет существующий отзыв по его идентификатору.
     *
     * @param id      уникальный идентификатор отзыва
     * @param rating  новый рейтинг отзыва
     * @param comment новый текст отзыва
     * @return обновленный отзыв
     */
    @MutationMapping
    public Mono<Review> updateReview(
            @Argument String id,
            @Argument Integer rating,
            @Argument String comment
    ) {
        return reviewService.updateReview(id, rating, comment);
    }

    /**
     * Удаляет отзыв по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор отзыва
     * @return результат операции удаления (true, если удаление прошло успешно)
     */
    @MutationMapping
    public Mono<Boolean> deleteReview(@Argument String id) {
        return reviewService.deleteReview(id);
    }

}
