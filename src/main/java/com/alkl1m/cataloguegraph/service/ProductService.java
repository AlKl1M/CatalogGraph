package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Сервис для управления продуктами.
 * Предоставляет методы для получения, добавления, обновления и удаления продуктов,
 * а также для добавления отзывов и расчета среднего рейтинга.
 *
 * @author AlKl1M
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Получает список продуктов с фильтрацией по категории и ценовому диапазону с пагинацией.
     *
     * @param category категория продуктов (может быть null)
     * @param minPrice минимальная цена продуктов (может быть null)
     * @param maxPrice максимальная цена продуктов (может быть null)
     * @param page номер страницы для пагинации
     * @param size количество элементов на странице
     * @return список продуктов, удовлетворяющих фильтрам
     */
    public Flux<Product> getProductsFiltered(String category, Float minPrice, Float maxPrice, int page, int size) {

        return productRepository.findAll()
                .filter(product -> (category == null || product.getCategory().equals(category)) &&
                        (minPrice == null || product.getPrice() >= minPrice) &&
                        (maxPrice == null || product.getPrice() <= maxPrice))
                .skip((long) page * size)
                .take(size);
    }

    /**
     * Получает продукт по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор продукта
     * @return продукт с указанным идентификатором
     */
    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    /**
     * Добавляет новый продукт.
     *
     * @param product объект продукта для добавления
     * @return добавленный продукт
     */
    public Mono<Product> addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Обновляет информацию о продукте.
     *
     * @param id уникальный идентификатор продукта
     * @param updatedProduct объект с обновленной информацией о продукте
     * @return обновленный продукт
     */
    public Mono<Product> updateProduct(String id, Product updatedProduct) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .flatMap(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setDescription(updatedProduct.getDescription());
                    product.setCategory(updatedProduct.getCategory());
                    return productRepository.save(product);
                });
    }

    /**
     * Удаляет продукт по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор продукта
     * @return результат операции удаления (true, если удаление прошло успешно)
     */
    public Mono<Boolean> deleteProduct(String id) {
        return productRepository.existsById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .flatMap(exist -> {
                    if (Boolean.TRUE.equals(exist)) {
                        return productRepository.deleteById(id)
                                .then(Mono.just(true));
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    /**
     * Добавляет отзыв к продукту.
     *
     * @param productId уникальный идентификатор продукта
     * @param review объект отзыва
     * @return обновленный продукт с добавленным отзывом
     */
    public Mono<Product> addReviewToProduct(String productId, Review review) {
        return productRepository.findById(productId)
                .flatMap(product -> {
                    product.getReviews().add(review);
                    return productRepository.save(product);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")));
    }

    /**
     * Получает средний рейтинг продукта на основе его отзывов.
     *
     * @param id уникальный идентификатор продукта
     * @return средний рейтинг продукта
     */
    public Mono<Double> getProductAverageRating(String id) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    double averageRating = product.getReviews().stream()
                            .mapToInt(Review::getRating)
                            .average().orElse(0);
                    return Mono.just(averageRating);
                });
    }

}
