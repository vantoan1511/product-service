package com.shopbee.productservice.shared;

import com.shopbee.productservice.exception.ProductServiceException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class ExternalServiceExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public ProductServiceException toThrowable(Response response) {
        Response.Status.Family family = response.getStatusInfo().getFamily();
        if (Response.Status.Family.SUCCESSFUL.equals(family)) {
            return null;
        }
        return new ProductServiceException(response.getEntity(), Response.Status.SERVICE_UNAVAILABLE);
    }
}
