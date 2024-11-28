package com.shopbee.productservice.mapper;

import com.shopbee.productservice.dto.ModelCreationRequest;
import com.shopbee.productservice.entity.Model;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ModelMapper {

    Model toModel(ModelCreationRequest modelCreationRequest);

    ModelCreationRequest toModelRequest(Model model);
}
