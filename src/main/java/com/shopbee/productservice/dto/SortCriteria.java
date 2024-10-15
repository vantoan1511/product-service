package com.shopbee.productservice.dto;

import com.shopbee.productservice.enums.SortField;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SortCriteria {

    @QueryParam("sortBy")
    private SortField sortField = SortField.CREATED_AT;

    @QueryParam("ascending")
    private boolean ascending;

}
