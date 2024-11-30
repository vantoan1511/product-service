package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.FilterCriteria;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.enums.SortField;
import com.shopbee.productservice.shared.QueryBuilder;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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

    public List<Product> findByCriteria(FilterCriteria filterCriteria,
                                        SortCriteria sortCriteria,
                                        PageRequest pageRequest) {
        QueryBuilder queryBuilder = queryBuilder(filterCriteria);
        PanacheQuery<Product> query = find(queryBuilder.getQueryString(), sort(sortCriteria), queryBuilder.getParameters())
                .page(paginate(pageRequest));
        return query.list();
    }

    public long count(FilterCriteria filterCriteria) {
        QueryBuilder queryBuilder = queryBuilder(filterCriteria);
        return count(queryBuilder.getQueryString(), queryBuilder.getParameters());
    }

    private QueryBuilder queryBuilder(FilterCriteria filterCriteria) {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.addInCondition("model.brand.slug", filterCriteria.getBrands());
        queryBuilder.addInCondition("model.slug", filterCriteria.getModels());
        queryBuilder.addInCondition("category.slug", filterCriteria.getCategories());
        queryBuilder.addGreaterThanOrEqualCondition("salePrice", filterCriteria.getMinPrice());
        queryBuilder.addLessThanOrEqualCondition("salePrice", filterCriteria.getMaxPrice());
        queryBuilder.addLikeCondition("lower(name)", filterCriteria.getKeyword());
        queryBuilder.addEqualsCondition("active", filterCriteria.getActive());
        return queryBuilder;
    }

    @Override
    protected List<SortField> getSupportedSortFields() {
        List<SortField> fields = new ArrayList<>(super.getSupportedSortFields());
        Collections.addAll(fields, SortField.BASE_PRICE, SortField.SALE_PRICE, SortField.VIEW_COUNT);
        return fields;
    }

}