package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.BrandCreationRequest;
import com.shopbee.productservice.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface BrandMapper {

    Brand toBrand(BrandCreationRequest brandCreationRequest);

    BrandCreationRequest toBrandCreation(Brand brand);
}
