package com.shopbee.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrandCreationRequest {

    @NotBlank(message = "Brand name is required")
    private String name;

    @NotBlank(message = "Brand name is required")
    private String slug;

    private String description;
}
