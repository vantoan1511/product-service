package com.shopbee.productservice.shared.converter.impl;

import com.shopbee.productservice.dto.ProductResponse;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.entity.ProductImage;
import com.shopbee.productservice.shared.converter.IConverter;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The type Product converter.
 */
@ApplicationScoped
public class ProductConverter implements IConverter<Product, ProductResponse> {

    @Override
    public ProductResponse convert(Product product) {
        if (Objects.isNull(product)) {
            return null;
        }

        List<Long> imageIds = getImageIds(product);
        Long featuredImageId = getFeaturedImageId(product);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .salePrice(product.getSalePrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .weight(product.getWeight())
                .color(product.getColor())
                .processor(product.getProcessor())
                .gpu(product.getGpu())
                .ram(product.getRam())
                .storageType(product.getStorageType())
                .storageCapacity(product.getStorageCapacity())
                .os(product.getOs())
                .screenSize(product.getScreenSize())
                .batteryCapacity(product.getBatteryCapacity())
                .warranty(product.getWarranty())
                .viewCount(product.getViewCount())
                .userId(product.getUserId())
                .model(product.getModel())
                .category(product.getCategory())
                .imageIds(imageIds)
                .featuredImageId(featuredImageId)
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }

    /**
     * Gets featured image id.
     *
     * @param product the product
     * @return the featured image id
     */
    private Long getFeaturedImageId(Product product) {
        return getProductImageStream(product).filter(ProductImage::isFeatured).map(ProductImage::getImageId).findFirst().orElse(null);
    }

    /**
     * Gets image ids.
     *
     * @param product the product
     * @return the image ids
     */
    private List<Long> getImageIds(Product product) {
        return getProductImageStream(product).map(ProductImage::getImageId).toList();
    }

    /**
     * Gets product image stream.
     *
     * @param product the product
     * @return the product image stream
     */
    private Stream<ProductImage> getProductImageStream(Product product) {
        return CollectionUtils.emptyIfNull(product.getImages()).stream();
    }
}
