package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.FilterCriteria;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.enums.SortField;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository extends AbstractRepository implements PanacheRepository<Product> {

    public Optional<Product> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }

    public List<Product> findByCriteria(FilterCriteria filterCriteria,
                                        SortCriteria sortCriteria,
                                        PageRequest pageRequest) {
        StringBuilder queryString = new StringBuilder("1=1");
        List<Object> parameters = new ArrayList<>();
        int paramIndex = 1;

        if (CollectionUtils.isNotEmpty(filterCriteria.getBrands())) {
            queryString.append(" and model.brand.slug IN ?").append(paramIndex++);
            parameters.add(filterCriteria.getBrands());
        }
        if (CollectionUtils.isNotEmpty(filterCriteria.getCategories())) {
            queryString.append(" and category.slug IN ?").append(paramIndex++);
            parameters.add(filterCriteria.getCategories());
        }
        if (filterCriteria.getMinPrice() != null) {
            queryString.append(" and salePrice >= ?").append(paramIndex++);
            parameters.add(filterCriteria.getMinPrice());
        }
        if (filterCriteria.getMaxPrice() != null) {
            queryString.append(" and salePrice <= ?").append(paramIndex++);
            parameters.add(filterCriteria.getMaxPrice());
        }
        if (StringUtils.isNotBlank(filterCriteria.getKeyword())) {
            queryString.append(" and (lower(name) LIKE ?").append(paramIndex).append(")");
            parameters.add("%" + filterCriteria.getKeyword().toLowerCase().trim() + "%");
        }
        PanacheQuery<Product> query = find(queryString.toString(), sort(sortCriteria), parameters.toArray()).page(paginate(pageRequest));

        return query.list();
    }

    public long count(FilterCriteria filterCriteria) {
        if (CollectionUtils.isNotEmpty(filterCriteria.getBrands())) {
            return count("model.brand.slug IN ?1", filterCriteria.getBrands());
        }
        return count();
    }

    @Override
    protected List<SortField> getSupportedSortFields() {
        List<SortField> fields = new ArrayList<>(super.getSupportedSortFields());
        Collections.addAll(fields, SortField.BASE_PRICE, SortField.SALE_PRICE, SortField.VIEW_COUNT);
        return fields;
    }

}