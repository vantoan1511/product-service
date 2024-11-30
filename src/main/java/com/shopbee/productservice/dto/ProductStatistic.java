package com.shopbee.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatistic {
    private long totalProducts;
    private long totalActiveProducts;
    private Map<String, Long> totalProductsByBrand;
    private Map<String, Long> totalProductsByModel;
    private Map<String, Long> totalProductsByCategory;
}
