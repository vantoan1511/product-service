package com.shopbee.productservice.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("health-check")
public class HealthCheckResource {

    @GET
    public Response checkHealth() {
        return Response.ok().build();
    }
    
}
