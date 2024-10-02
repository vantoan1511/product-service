package com.shopbee.productservice.entity;

import com.shopbee.productservice.entity.enums.Color;
import com.shopbee.productservice.entity.enums.OS;
import com.shopbee.productservice.entity.enums.StorageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ps_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(length = 60, nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column
    private boolean active = true;

    @Column
    private Long weight;

    @Column
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column
    private String processor;

    @Column
    private String gpu;

    @Column
    private Integer ram;

    @Column(name = "storage_type")
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    @Column(name = "storage_capacity")
    private Integer storageCapacity;

    @Column
    @Enumerated(EnumType.STRING)
    private OS os;

    @Column(name = "screen_size")
    private Long screenSize;

    @Column(name = "battery_capacity")
    private Long batteryCapacity;

    @Column
    private Integer warranty;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "modified_at")
    @UpdateTimestamp
    private OffsetDateTime modifiedAt;
}
