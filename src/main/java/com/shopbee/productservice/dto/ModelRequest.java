package com.shopbee.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelRequest {
    @NotBlank(message = "Brand name is required")
    private String name;

    @NotBlank(message = "Brand name is required")
    private String slug;

    private String description;

    @NotNull(message = "Brand is required")
    private String brandSlug;
}
