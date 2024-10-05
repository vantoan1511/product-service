package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.CategoryRequest;
import com.shopbee.productservice.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface CategoryMapper {

    Category toCategory(CategoryRequest categoryRequest);

}
