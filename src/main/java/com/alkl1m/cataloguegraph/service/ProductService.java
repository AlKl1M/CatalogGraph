package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> getProductsFiltered(String category, Float minPrice, Float maxPrice, int page, int size);

    Mono<Product> getProductById(String id);

    Mono<Product> addProduct(Product product);

    Mono<Product> updateProduct(String id, Product updatedProduct);

    Mono<Boolean> deleteProduct(String id);

    Mono<Product> addReviewToProduct(String productId, Review review);

    Mono<Double> getProductAverageRating(String id);
}
