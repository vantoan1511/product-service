package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.enums.SortField;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import java.util.List;

public abstract class AbstractRepository {

    public Sort sort(SortCriteria sortCriteria) {
        SortField sortField = sortCriteria.getSortField();
        if (!getSupportedSortFields().contains(sortField)) {
            return Sort.empty();
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
        return List.of(SortField.values());
    }
}
