package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.ModelRequest;
import com.shopbee.productservice.entity.Model;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ModelMapper {

    Model toModel(ModelRequest modelRequest);

    ModelRequest toModelRequest(Model model);
}
