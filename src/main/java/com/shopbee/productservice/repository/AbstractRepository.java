package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.enums.SortField;
import com.shopbee.productservice.exception.ProductServiceException;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractRepository {

    public Sort sort(SortCriteria sortCriteria) {
        SortField sortField = sortCriteria.getSortField();
        if (!getSupportedSortFields().contains(sortField)) {
            throw new ProductServiceException("Unsupported sort field", Response.Status.BAD_REQUEST);
        }

        Sort sort = Sort.by(sortField.getValue());
        if (sortCriteria.isAscending()) {
            return sort.ascending();
        }
        return sort.descending();
    }

    public Page paginate(PageRequest pageRequest) {
        return Page.of(pageRequest.getPage() - 1, pageRequest.getSize());
    }

    protected List<SortField> getSupportedSortFields() {
        return List.of(SortField.NAME, SortField.SLUG, SortField.CREATED_AT);
    }
}
