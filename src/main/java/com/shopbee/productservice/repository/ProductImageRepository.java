package com.shopbee.productservice.repository;

import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.entity.ProductImage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductImageRepository extends AbstractRepository implements PanacheRepository<ProductImage> {

    public ProductImage findByProductAndImageId(Product product, Long imageId) {
        return find("product = ?1 AND imageId = ?2", product, imageId).firstResult();
    }

    public void deleteByProductAndImageId(Product product, Long imageId) {
        delete("product = ?1 AND imageId = ?2", product, imageId);
    }

    public List<ProductImage> findByProduct(Product product) {
        return find("product", product).list();
    }
}
