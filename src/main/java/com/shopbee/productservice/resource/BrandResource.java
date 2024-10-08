package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.BrandRequest;
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
public class BrandResource {

    BrandService brandService;

    public BrandResource(BrandService brandService) {
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
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response create(BrandRequest brandRequest, @Context UriInfo uriInfo) {
        Brand brand = brandService.create(brandRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(brand.getSlug()).build();
        return Response.created(uri).entity(brand).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response update(@PathParam("id") Long id, BrandRequest brandRequest) {
        brandService.update(id, brandRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response delete(List<Long> ids) {
        brandService.delete(ids);
        return Response.noContent().build();
    }
}
