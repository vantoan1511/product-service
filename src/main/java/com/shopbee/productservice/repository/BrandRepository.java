package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.enums.SortField;
import com.shopbee.productservice.exception.ProductServiceException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.*;

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