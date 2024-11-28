package com.shopbee.productservice.shared;

import com.shopbee.productservice.exception.ProductServiceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class ExternalServiceExceptionMapper implements ResponseExceptionMapper<ProductServiceException> {

    @Override
    public ProductServiceException toThrowable(Response response) {
        Response.Status.Family family = response.getStatusInfo().getFamily();
        if (Response.Status.Family.SUCCESSFUL.equals(family)) {
            return null;
        }
        return new ProductServiceException("Failed to call external service.", Response.Status.SERVICE_UNAVAILABLE);
    }
}
