package com.shopbee.productservice.shared.external.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Integer rating;
    private String text;
    private Integer helpfulCount;
    private Instant createdAt;
    private String username;
    private String productSlug;
}
