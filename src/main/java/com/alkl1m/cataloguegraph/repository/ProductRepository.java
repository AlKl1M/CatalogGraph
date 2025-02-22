package com.alkl1m.cataloguegraph.repository;

import com.alkl1m.cataloguegraph.entity.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author AlKl1M
 */
@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
