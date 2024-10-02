package com.shopbee.productservice.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.Map;

public class ProductServiceException extends WebApplicationException {

    public ProductServiceException(String message, Response.Status status) {
        super(message, status);
    }

    public ProductServiceException(Object messages, Response.Status status) {
        super(Response.status(status).entity(messages).build());
    }


}
