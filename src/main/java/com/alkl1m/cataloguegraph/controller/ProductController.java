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

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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

    @QueryMapping
    public Mono<Product> getProductById(@Argument String id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public Mono<Product> addProduct(
            @Argument String name,
            @Argument String description,
            @Argument Double price,
            @Argument String category) {
        Product product = new Product(null, name, description, price, category, null);
        return productService.addProduct(product);
    }

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

    @MutationMapping
    public Mono<Boolean> deleteProduct(@Argument String id) {
        return productService.deleteProduct(id);
    }

    @QueryMapping
    public Mono<Double> getProductAverageRating(@Argument String id) {
        return productService.getProductAverageRating(id);
    }

}
