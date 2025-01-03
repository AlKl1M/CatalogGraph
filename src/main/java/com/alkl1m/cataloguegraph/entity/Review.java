package com.alkl1m.cataloguegraph.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Сущность отзыва.
 *
 * @author AlKl1M
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;
    private String productId;
    private String author;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

}
