package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Model;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ModelRepository extends AbstractRepository implements PanacheRepository<Model> {

    public List<Model> findByCriteria(SortCriteria sortCriteria, PageRequest pageRequest) {
        return findAll(sort(sortCriteria))
                .page(paginate(pageRequest))
                .list();
    }

    public Optional<Model> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }
}