package com.shopbee.productservice.service.user;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("users")
@RegisterRestClient(configKey = "userResource")
public interface UserResource {

    @GET
    @Path("{id}")
    Response getById(@PathParam("id") Long id, @HeaderParam(HttpHeaders.AUTHORIZATION) String bearerToken);

    @GET
    Response search(@QueryParam("keyword") String keyword, @HeaderParam(HttpHeaders.AUTHORIZATION) String bearerToken);
}
