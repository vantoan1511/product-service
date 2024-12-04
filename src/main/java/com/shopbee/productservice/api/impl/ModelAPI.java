package com.shopbee.productservice.api.impl;

import com.shopbee.productservice.dto.ModelCreationRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Model;
import com.shopbee.productservice.security.constant.Role;
import com.shopbee.productservice.service.ModelService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelAPI {

    ModelService modelService;

    public ModelAPI(ModelService modelService) {
        this.modelService = modelService;
    }

    @GET
    public Response getByCriteria(@BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(modelService.getByCriteria(sortCriteria, pageRequest)).build();
    }

    @GET
    @Path("{slug}")
    public Response getBySlug(@PathParam("slug") String slug) {
        return Response.ok(modelService.getBySlug(slug)).build();
    }

    @POST
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response create(ModelCreationRequest modelCreationRequest, @Context UriInfo uriInfo) {
        Model model = modelService.create(modelCreationRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(model.getSlug()).build();
        return Response.created(uri).entity(model).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response update(@PathParam("id") Long id, ModelCreationRequest modelCreationRequest) {
        modelService.update(id, modelCreationRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response delete(List<Long> ids) {
        modelService.delete(ids);
        return Response.noContent().build();
    }
}
