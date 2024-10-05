package com.shopbee.productservice.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantityRequest {
    private Long id;
    @Min(value = 0, message = "Quantity must be greater than or equal 0")
    private int quantity;
}
