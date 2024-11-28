package com.shopbee.productservice.dto;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {

    @QueryParam("brands")
    private String brandListString;

    @QueryParam("categories")
    private String categoryListString;

    @QueryParam("minPrice")
    private BigDecimal minPrice;

    @QueryParam("maxPrice")
    private BigDecimal maxPrice;

    @QueryParam("keyword")
    private String keyword;

    @QueryParam("active")
    private Boolean active;

    private List<String> brands;
    private List<String> categories;
}
