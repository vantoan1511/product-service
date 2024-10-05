package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.enums.SortField;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository extends AbstractRepository implements PanacheRepository<Product> {

    public Optional<Product> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

    public List<Product> findByCriteria(SortCriteria sortCriteria, PageRequest pageRequest) {
        return findAll(sort(sortCriteria))
                .page(paginate(pageRequest))
                .list();
    }

    @Override
    protected List<SortField> getSupportedSortFields() {
        List<SortField> fields = new ArrayList<>(super.getSupportedSortFields());
        Collections.addAll(fields, SortField.BASE_PRICE, SortField.SALE_PRICE, SortField.VIEW_COUNT);
        return fields;
    }
}