package com.shopbee.productservice.api.impl;

import com.shopbee.productservice.dto.BrandCreationRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.security.constant.Role;
import com.shopbee.productservice.service.BrandService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("brands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrandAPI {

    BrandService brandService;

    public BrandAPI(BrandService brandService) {
        this.brandService = brandService;
    }

    @GET
    public Response getByCriteria(@BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(brandService.getByCriteria(sortCriteria, pageRequest)).build();
    }

    @GET
    @Path("{slug}")
    public Response getBySlug(@PathParam("slug") String slug) {
        return Response.ok(brandService.getBySlug(slug)).build();
    }

    @POST
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response create(BrandCreationRequest brandCreationRequest, @Context UriInfo uriInfo) {
        Brand brand = brandService.create(brandCreationRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(brand.getSlug()).build();
        return Response.created(uri).entity(brand).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response update(@PathParam("id") Long id, BrandCreationRequest brandCreationRequest) {
        brandService.update(id, brandCreationRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    public Response delete(List<Long> ids) {
        brandService.delete(ids);
        return Response.noContent().build();
    }
}
