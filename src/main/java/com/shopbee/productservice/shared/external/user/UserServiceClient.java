package com.shopbee.productservice.shared.external.user;

import com.shopbee.productservice.shared.Constant;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("users")
@RegisterRestClient(configKey = Constant.USER_SERVICE_CLIENT)
@RegisterClientHeaders
public interface UserServiceClient {

    @GET
    Response search(@QueryParam("keyword") String keyword);
}
