package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @QueryMapping
    public Flux<Product> getProducts() {
        return productService.getAllProducts();
    }

    @QueryMapping
    public Mono<Product> getProductById(String id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public Mono<Product> addProduct(Product product) {
        return productService.addProduct(product);
    }

    @MutationMapping
    public Mono<Product> updateProduct(String id, Product product) {
        return productService.updateProduct(id, product);
    }

    @MutationMapping
    public Mono<Boolean> deleteProduct(String id) {
        return productService.deleteProduct(id);
    }

}