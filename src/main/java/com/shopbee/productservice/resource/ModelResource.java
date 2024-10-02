package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.ModelRequest;
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
public class ModelResource {

    ModelService modelService;

    public ModelResource(ModelService modelService) {
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
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response create(ModelRequest modelRequest, @Context UriInfo uriInfo) {
        Model model = modelService.create(modelRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(model.getSlug()).build();
        return Response.created(uri).entity(model).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response update(@PathParam("id") Long id, ModelRequest modelRequest) {
        modelService.update(id, modelRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response delete(List<Long> ids) {
        modelService.delete(ids);
        return Response.noContent().build();
    }
}
