package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @QueryMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @QueryMapping
    public Product getProductById(String id) {
        return productService.getProductById(id).orElse(null);
    }

    @MutationMapping
    public Product addProduct(Product product) {
        return productService.addProduct(product);
    }

    @MutationMapping
    public Product updateProduct(String id, Product product) {
        return productService.updateProduct(id, product);
    }

    @MutationMapping
    public Boolean deleteProduct(String id) {
        return productService.deleteProduct(id);
    }
}