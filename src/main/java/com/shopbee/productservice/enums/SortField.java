package com.shopbee.productservice.enums;

import lombok.Getter;

@Getter
public enum SortField {
    NAME("name"),
    SLUG("slug"),
    CREATED_AT("createdAt"),
    MODIFIED_AT("modifiedAt");

    final String value;

    SortField(String value) {
        this.value = value;
    }

}
