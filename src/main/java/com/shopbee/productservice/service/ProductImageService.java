package com.shopbee.productservice.service;

import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.entity.ProductImage;
import com.shopbee.productservice.exception.ProductServiceException;
import com.shopbee.productservice.repository.ProductImageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Transactional
public class ProductImageService {

    ProductImageRepository productImageRepository;
    ProductService productService;

    public ProductImageService(ProductImageRepository productImageRepository,
                               ProductService productService) {
        this.productImageRepository = productImageRepository;
        this.productService = productService;
    }

    public List<ProductImage> getProductImages(Long productId) {
        Product product = productService.getById(productId);
        return productImageRepository.findByProduct(product);
    }

    public void setFeaturedImage(Long productId, Long imageId) {
        Product product = productService.getById(productId);
        ProductImage productImage = productImageRepository.findByProductAndImageId(product, imageId);
        if (productImage == null) {
            throw new ProductServiceException("Image not found", Response.Status.NOT_FOUND);
        }

        List<ProductImage> productImages = productImageRepository.findByProduct(product);
        productImages.forEach(each -> each.setFeatured(false));
        productImage.setFeatured(true);
    }

    public void updateImages(Long productId, List<Long> imageIds) {
        Product product = productService.getById(productId);
        List<ProductImage> productImages = imageIds.stream().map(imageId -> {
            if (productImageRepository.findByProductAndImageId(product, imageId) == null) {
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageId(imageId);
                return productImage;
            }
            return null;
        }).filter(Objects::nonNull).toList();
        productImageRepository.persist(productImages);
    }

    public void removeImages(Long productId, List<Long> imageIds) {
        imageIds.forEach(id -> removeImage(productId, id));
    }

    public void removeImage(Long productId, Long imageId) {
        Product product = productService.getById(productId);
        productImageRepository.deleteByProductAndImageId(product, imageId);
    }
}
