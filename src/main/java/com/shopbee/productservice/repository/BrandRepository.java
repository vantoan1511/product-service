package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BrandRepository extends AbstractRepository implements PanacheRepository<Brand> {

    public List<Brand> findByCriteria(SortCriteria sortCriteria, PageRequest pageRequest) {
        return findAll(sort(sortCriteria))
                .page(paginate(pageRequest))
                .list();
    }

    public boolean existBySlug(String slug) {
        return findBySlug(slug).isPresent();
    }

    public Optional<Brand> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

}