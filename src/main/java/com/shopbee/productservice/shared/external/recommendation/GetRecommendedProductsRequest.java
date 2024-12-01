package com.shopbee.productservice.shared.external.recommendation;

import com.shopbee.productservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRecommendedProductsRequest {

    private Behavior behavior;
    private List<Product> availableProducts;
}
