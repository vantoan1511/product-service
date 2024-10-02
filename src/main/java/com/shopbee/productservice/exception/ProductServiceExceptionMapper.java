package com.shopbee.productservice.exception;

import com.shopbee.productservice.dto.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProductServiceExceptionMapper implements ExceptionMapper<ProductServiceException> {

    @Override
    public Response toResponse(ProductServiceException e) {
        return Response.status(e.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(e.getMessage(), null))
                .build();
    }

}
