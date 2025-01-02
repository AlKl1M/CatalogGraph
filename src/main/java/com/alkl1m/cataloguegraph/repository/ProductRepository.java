package com.alkl1m.cataloguegraph.repository;

import com.alkl1m.cataloguegraph.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

}
