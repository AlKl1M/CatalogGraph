package com.alkl1m.cataloguegraph.controller;

import com.alkl1m.cataloguegraph.entity.Product;
import com.alkl1m.cataloguegraph.entity.Review;
import com.alkl1m.cataloguegraph.repository.ProductRepository;
import com.alkl1m.cataloguegraph.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
@DisplayName("Интеграционные тесты для контроллера продуктов")
class ProductControllerTest {

    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "testDB");
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll().subscribe();
        reviewRepository.deleteAll().subscribe();

        Product product1 = new Product(null, "Laptop", "High-end laptop", 1500.0, "Electronics", null);
        Product product2 = new Product(null, "Headphones", "Noise-cancelling headphones", 300.0, "Electronics", null);
        productRepository.saveAll(List.of(product1, product2)).collectList().block();

        Review review1 = new Review(null, product1.getId(), "User1", 5, "Excellent!", LocalDateTime.now());
        Review review2 = new Review(null, product1.getId(), "User2", 4, "Very good!", LocalDateTime.now());
        reviewRepository.saveAll(List.of(review1, review2)).collectList().block();
    }

    @Test
    @DisplayName("Должен извлечь продукты по категории")
    void testGetProductsByCategory() {
        String query = """
                    query {
                        getProducts(category: \"Electronics\", page: 0, size: 10) {
                            id
                            name
                            description
                            price
                            category
                        }
                    }
                """;

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getProducts").isArray()
                .jsonPath("$.data.getProducts[0].category").isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Должен извлечь продукт по ID")
    void testGetProductById() {
        Product product = productRepository.findAll().blockFirst();
        assertNotNull(product);

        String query = String.format("""
                    query {
                        getProductById(id: \"%s\") {
                            id
                            name
                            description
                            price
                        }
                    }
                """, product.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getProductById.id").isEqualTo(product.getId());
    }

    @Test
    @DisplayName("Должен добавить новый продукт")
    void testAddProduct() {
        String mutation = """
                    mutation {
                        addProduct(name: \"Smartphone\", description: \"Latest model\", price: 999.99, category: \"Electronics\") {
                            id
                            name
                            description
                            price
                            category
                        }
                    }
                """;

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.addProduct.name").isEqualTo("Smartphone");
    }

    @Test
    @DisplayName("Должен удалить продукт")
    void testDeleteProduct() {
        Product product = productRepository.findAll().blockFirst();
        assertNotNull(product);

        String mutation = String.format("""
                    mutation {
                        deleteProduct(id: \"%s\")
                    }
                """, product.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.deleteProduct").isEqualTo(true);
    }


    @Test
    @DisplayName("Должен обновить продукт")
    void testUpdateProduct_withValidPayload_ReturnsValidData() {
        Product product = productRepository.findAll().blockFirst();
        assertNotNull(product);

        String updatedName = "Updated Laptop";
        String updatedDescription = "Updated description for the high-end laptop";
        Double updatedPrice = 1800.0;
        String updatedCategory = "Electronics";

        String mutation = String.format("""
                    mutation {
                        updateProduct(id: \"%s\", name: \"%s\", description: \"%s\", price: %s, category: \"%s\") {
                            id
                            name
                            description
                            price
                            category
                        }
                    }
                """, product.getId(), updatedName, updatedDescription, updatedPrice, updatedCategory);

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.updateProduct.id").isEqualTo(product.getId())
                .jsonPath("$.data.updateProduct.name").isEqualTo(updatedName)
                .jsonPath("$.data.updateProduct.description").isEqualTo(updatedDescription)
                .jsonPath("$.data.updateProduct.price").isEqualTo(updatedPrice)
                .jsonPath("$.data.updateProduct.category").isEqualTo(updatedCategory);
    }

    @Test
    @DisplayName("Должен извлечь средний рейтинг продукта")
    void testGetProductAverageRating_withValidPayload_ReturnsValidAgerageRating() {
        Product product = productRepository.findAll().blockFirst();
        assertNotNull(product);

        Review review = new Review(null, product.getId(), "User3", 4, "Good", LocalDateTime.now());

        product.getReviews().add(review);

        reviewRepository.save(review).block();
        productRepository.save(product).block();

        String query = String.format("""
                    query {
                        getProductAverageRating(id: \"%s\")
                    }
                """, product.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getProductAverageRating").isEqualTo(4);
    }

}

