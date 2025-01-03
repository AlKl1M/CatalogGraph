package com.alkl1m.cataloguegraph.repository;

import com.alkl1m.cataloguegraph.entity.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @author AlKl1M
 */
@Repository
public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findByProductId(String productId);

}
