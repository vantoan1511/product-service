package com.shopbee.productservice.dto;

import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.entity.enums.Color;
import com.shopbee.productservice.entity.enums.OS;
import com.shopbee.productservice.entity.enums.StorageType;
import com.shopbee.productservice.shared.external.review.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private boolean active;
    private Double weight;
    private Color color;
    private String processor;
    private String gpu;
    private Integer ram;
    private StorageType storageType;
    private Integer storageCapacity;
    private OS os;
    private Double screenSize;
    private Double batteryCapacity;
    private Double warranty;
    private Long viewCount;
    private Long userId;
    private Model model;
    private Category category;
    private List<Long> imageIds;
    private Long featuredImageId;
    private double rating;
    private OffsetDateTime createdAt;
    private OffsetDateTime modifiedAt;
}
