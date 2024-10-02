package com.shopbee.productservice.repository;

import com.shopbee.productservice.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public Optional<Product> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

    public List<Product> findByCriteria(int offset, int limit) {
        return findAll().page(Page.of(offset, limit)).list();
    }

    public int getTotalPages(int pageSize) {
        long totalProducts = countAllProducts();
        return (int) Math.ceil((double) totalProducts / pageSize);
    }

    public long countAllProducts() {
        return count();
    }


}