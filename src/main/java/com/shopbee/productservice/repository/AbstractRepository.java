package com.shopbee.productservice.repository;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

public class AbstractRepository {
    public Sort sort(SortCriteria sortCriteria) {
        String sortColumn = sortCriteria.getSortField().getValue();
        Sort sort = Sort.by(sortColumn);

        if (sortCriteria.isAscending()) {
            return sort.ascending();
        }

        return sort.descending();
    }

    public Page paginate(PageRequest pageRequest) {
        return Page.of(pageRequest.getPage() - 1, pageRequest.getSize());
    }
}
