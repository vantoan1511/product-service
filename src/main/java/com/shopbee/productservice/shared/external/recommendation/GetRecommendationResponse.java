package com.shopbee.productservice.shared.external.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRecommendationResponse {
    private List<EvaluatedProduct> recommendations;
}
