package com.shopbee.productservice.shared;

import jakarta.enterprise.context.RequestScoped;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequestScoped
public class QueryBuilder {
    private final StringBuilder queryString = new StringBuilder("1=1");
    private final List<Object> parameters = new ArrayList<>();
    private int parameterIndex = 1;

    public void addInCondition(String field, List<?> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            queryString.append(" and ").append(field).append(" IN ?").append(parameterIndex++);
            parameters.add(values);
        }
    }

    public void addGreaterThanOrEqualCondition(String field, Object value) {
        if (Objects.nonNull(value)) {
            queryString.append(" and ").append(field).append(" >= ?").append(parameterIndex++);
            parameters.add(value);
        }
    }

    public void addLessThanOrEqualCondition(String field, Object value) {
        if (Objects.nonNull(value)) {
            queryString.append(" and ").append(field).append(" <= ?").append(parameterIndex++);
            parameters.add(value);
        }
    }

    public void addLikeCondition(String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            queryString.append(" and ").append(field).append(" LIKE ?").append(parameterIndex++);
            parameters.add("%" + value.trim().toLowerCase() + "%");
        }
    }

    public void addEqualsCondition(String field, Object value) {
        if (Objects.nonNull(value)) {
            queryString.append(" and ").append(field).append(" = ?").append(parameterIndex++);
            parameters.add(value);
        }
    }

    public String getQueryString() {
        return queryString.toString();
    }

    public Object[] getParameters() {
        return parameters.toArray();
    }
}
