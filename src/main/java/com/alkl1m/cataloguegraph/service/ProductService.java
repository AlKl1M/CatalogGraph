package com.alkl1m.cataloguegraph.service;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
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
                    if (exist) {
                        return productRepository.deleteById(id)
                                .then(Mono.just(true));
                    } else {
                        return Mono.just(false);
                    }
                });
    }
}