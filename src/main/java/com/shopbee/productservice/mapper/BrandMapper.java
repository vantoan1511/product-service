package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.BrandRequest;
import com.shopbee.productservice.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface BrandMapper {

    Brand toBrand(BrandRequest brandRequest);

    BrandRequest toBrandCreation(Brand brand);
}
