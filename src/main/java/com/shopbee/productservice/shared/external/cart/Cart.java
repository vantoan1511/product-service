package com.shopbee.productservice.shared.external.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private int quantity;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
    private String username;
    private String productSlug;
}
