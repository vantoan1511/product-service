package com.shopbee.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Length(max = 60, message = "Max length is 60 character")
    private String name;

    @NotBlank(message = "Slug is required")
    @Length(max = 60, message = "Max length is 60 characters")
    private String slug;

    @Length(max = 1000, message = "Max length is 1000 characters")
    private String description;

    @Min(value = 0, message = "Price must be greater than or equal 0")
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Min(value = 0, message = "Price must be greater than or equal 0")
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Min(value = 0, message = "Stock quantity must be greater than or equal 0")
    private Integer stockQuantity = 0;

    private boolean active = true;

    @Min(value = 0, message = "Weight must be greater than or equal 0")
    private double weight;

    private String color;

    private String processor;

    private String gpu;

    private int ram;

    private String storageType;

    private int storageCapacity;

    private String os;

    private double screenSize;

    private double batteryCapacity;

    private double warranty;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Category is required")
    private String category;
}
