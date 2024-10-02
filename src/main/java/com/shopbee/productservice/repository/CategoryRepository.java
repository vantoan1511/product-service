package com.shopbee.productservice.repository;

import com.shopbee.productservice.entity.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {

    public Optional<Category> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

}