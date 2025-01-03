package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Flux<Product> getProductsFiltered(String category, Float minPrice, Float maxPrice, int page, int size) {

        return productRepository.findAll()
                .filter(product -> (category == null || product.getCategory().equals(category)) &&
                        (minPrice == null || product.getPrice() >= minPrice) &&
                        (maxPrice == null || product.getPrice() <= maxPrice))
                .skip((long) page * size)
                .take(size);
    }

    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Mono<Product> addProduct(Product product) {
        return productRepository.save(product);
    }

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

    public Mono<Product> addReviewToProduct(String productId, Review review) {
        return productRepository.findById(productId)
                .flatMap(product -> {
                    product.getReviews().add(review);
                    return productRepository.save(product);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")));
    }

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
