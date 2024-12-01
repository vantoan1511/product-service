package com.shopbee.productservice.shared.external.cart;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.shared.Constant;
import com.shopbee.productservice.shared.ExternalServiceExceptionMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("carts")
@RegisterRestClient(configKey = Constant.CART_SERVICE_CLIENT)
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(ExternalServiceExceptionMapper.class)
public interface CartServiceClient {

    @GET
    PagedResponse<Cart> getCurrent(PageRequest pageRequest);
}
