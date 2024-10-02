package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.service.ProductService;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("products")
public class ProductResource {

    ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public Response getByCriteria(@BeanParam PageRequest pageRequest) {
        return Response.ok(productService.getByCriteria(pageRequest)).build();
    }


}
