package com.shopbee.productservice.shared.external.recommendation;

import com.shopbee.productservice.dto.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEvaluateResponse {
    private List<ProductResponse> recommendations;
}
