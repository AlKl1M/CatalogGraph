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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@AutoConfigureWebTestClient
@DisplayName("Интеграционные тесты для контроллера отзывов")
class ReviewControllerTest {

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
    private ReviewRepository reviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll().block();
        productRepository.deleteAll().block();

        Product product1 = new Product("product1", "Laptop", "High-end laptop", 1500.0, "Electronics", null);
        Product product2 = new Product("product2", "Laptop", "High-end laptop", 1500.0, "Electronics", null);

        productRepository.saveAll(List.of(product1, product2)).collectList().block();

        Review review1 = new Review(null, "product1", "User1", 5, "Excellent!", LocalDateTime.now());
        Review review2 = new Review(null, "product2", "User2", 4, "Very good!", LocalDateTime.now());
        reviewRepository.saveAll(List.of(review1, review2)).collectList().block();
    }

    @Test
    @DisplayName("Должен извлечь отзывы по ID продукта")
    void testGetReviewsByProductId() {
        String query = """
            query {
                getReviews(productId: \"product1\", page: 0, size: 10) {
                    id
                    author
                    rating
                    comment
                    createdAt
                }
            }
        """;

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getReviews").isArray();
    }

    @Test
    @DisplayName("Должен извлечь отзыв по ID")
    void testGetReviewById() {
        Review review = reviewRepository.findAll().blockFirst();
        assertNotNull(review);

        String query = String.format("""
            query {
                getReviewById(id: \"%s\") {
                    id
                    author
                    rating
                    comment
                }
            }
        """, review.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", query))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getReviewById.id").isEqualTo(review.getId());
    }

    @Test
    @DisplayName("Должен добавить новый отзыв")
    void testAddReview() {
        String mutation = """
            mutation {
                addReview(productId: \"product1\", author: \"User3\", rating: 5, comment: \"Amazing!\") {
                    id
                    author
                    rating
                    comment
                }
            }
        """;

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.addReview.author").isEqualTo("User3");
    }

    @Test
    @DisplayName("Должен обновить отзыв")
    void testUpdateReview() {
        Review review = reviewRepository.findAll().blockFirst();
        assertNotNull(review);

        String mutation = String.format("""
            mutation {
                updateReview(id: \"%s\", rating: 3, comment: \"Updated comment\") {
                    id
                    rating
                    comment
                }
            }
        """, review.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.updateReview.rating").isEqualTo(3)
                .jsonPath("$.data.updateReview.comment").isEqualTo("Updated comment");
    }

    @Test
    @DisplayName("Должен удалить отзыв")
    void testDeleteReview() {
        Review review = reviewRepository.findAll().blockFirst();
        assertNotNull(review);

        String mutation = String.format("""
            mutation {
                deleteReview(id: \"%s\")
            }
        """, review.getId());

        webTestClient.post()
                .uri("/graphql")
                .bodyValue(Map.of("query", mutation))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.deleteReview").isEqualTo(true);
    }
}
