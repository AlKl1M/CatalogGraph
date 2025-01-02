package com.alkl1m.cataloguegraph.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private List<Review> reviews = new ArrayList<>();

}
