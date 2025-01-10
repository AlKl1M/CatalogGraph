package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Контроллер для управления продуктами.
 * Предоставляет методы для получения, добавления, обновления и удаления продуктов.
 *
 * @author AlKl1M
 */
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Получает список продуктов с фильтрацией по категории и ценовому диапазону.
     *
     * @param category категория продуктов (может быть null)
     * @param minPrice минимальная цена продуктов (может быть null)
     * @param maxPrice максимальная цена продуктов (может быть null)
     * @param page     номер страницы для пагинации (по умолчанию 0)
     * @param size     количество элементов на странице (по умолчанию 10)
     * @return список продуктов, удовлетворяющих фильтрам
     */
    @QueryMapping
    public Flux<Product> getProducts(
            @Argument String category,
            @Argument Float minPrice,
            @Argument Float maxPrice,
            @Argument Integer page,
            @Argument Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        return productService.getProductsFiltered(category, minPrice, maxPrice, pageNumber, pageSize);
    }

    /**
     * Получает продукт по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор продукта
     * @return продукт с указанным идентификатором
     */
    @QueryMapping
    public Mono<Product> getProductById(@Argument String id) {
        return productService.getProductById(id);
    }

    /**
     * Добавляет новый продукт.
     *
     * @param name        название продукта
     * @param description описание продукта
     * @param price       цена продукта
     * @param category    категория продукта
     * @return добавленный продукт
     */
    @MutationMapping
    public Mono<Product> addProduct(
            @Argument String name,
            @Argument String description,
            @Argument Double price,
            @Argument String category) {
        Product product = new Product(null, name, description, price, category, null);
        return productService.addProduct(product);
    }

    /**
     * Обновляет информацию о продукте.
     *
     * @param id          уникальный идентификатор продукта
     * @param name        новое название продукта
     * @param description новое описание продукта
     * @param price       новая цена продукта
     * @param category    новая категория продукта
     * @return обновленный продукт
     */
    @MutationMapping
    public Mono<Product> updateProduct(
            @Argument String id,
            @Argument String name,
            @Argument String description,
            @Argument Double price,
            @Argument String category) {
        Product product = new Product(id, name, description, price, category, null);
        return productService.updateProduct(id, product);
    }

    /**
     * Удаляет продукт по уникальному идентификатору.
     *
     * @param id уникальный идентификатор продукта
     * @return результат операции удаления (true, если удаление прошло успешно)
     */
    @MutationMapping
    public Mono<Boolean> deleteProduct(@Argument String id) {
        return productService.deleteProduct(id);
    }

    /**
     * Получает средний рейтинг продукта.
     *
     * @param id уникальный идентификатор продукта
     * @return средний рейтинг продукта
     */
    @QueryMapping
    public Mono<Double> getProductAverageRating(@Argument String id) {
        return productService.getProductAverageRating(id);
    }

}
