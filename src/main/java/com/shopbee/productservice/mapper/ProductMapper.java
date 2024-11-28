package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.ProductCreationRequest;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.entity.enums.Color;
import com.shopbee.productservice.entity.enums.OS;
import com.shopbee.productservice.entity.enums.StorageType;
import com.shopbee.productservice.exception.ProductServiceException;
import jakarta.ws.rs.core.Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "os", source = "os", qualifiedByName = "toOS")
    @Mapping(target = "color", source = "color", qualifiedByName = "toColor")
    @Mapping(target = "storageType", source = "storageType", qualifiedByName = "toStorageType")
    Product toProduct(ProductCreationRequest productCreationRequest);

    @Named("toOS")
    static OS toOS(String os) {
        try {
            return OS.valueOf(os.replaceAll("\s", "").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductServiceException("Invalid os", Response.Status.BAD_REQUEST);
        }
    }

    @Named("toColor")
    static Color toColor(String color) {
        try {
            return Color.valueOf(color.replaceAll("\s", "").toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new ProductServiceException("Invalid color", Response.Status.BAD_REQUEST);
        }
    }

    @Named("toStorageType")
    static StorageType toStorageType(String storageType) {
        try {
            return StorageType.valueOf(storageType.replaceAll("\s", "").toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new ProductServiceException("Invalid storage type", Response.Status.BAD_REQUEST);
        }
    }
}
