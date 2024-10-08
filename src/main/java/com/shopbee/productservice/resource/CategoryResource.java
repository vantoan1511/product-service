package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.CategoryRequest;
import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Brand;
import com.shopbee.productservice.entity.Category;
import com.shopbee.productservice.security.constant.Role;
import com.shopbee.productservice.service.CategoryService;
import com.shopbee.productservice.service.CategoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    CategoryService categoryService;

    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GET
    public Response getByCriteria(@BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(categoryService.getByCriteria(sortCriteria, pageRequest)).build();
    }

    @GET
    @Path("{slug}")
    public Response getBySlug(@PathParam("slug") String slug) {
        return Response.ok(categoryService.getBySlug(slug)).build();
    }

    @POST
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response create(CategoryRequest categoryRequest, @Context UriInfo uriInfo) {
        Category category = categoryService.create(categoryRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(category.getSlug()).build();
        return Response.created(uri).entity(category).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response update(@PathParam("id") Long id, CategoryRequest categoryRequest) {
        categoryService.update(id, categoryRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response delete(List<Long> ids) {
        categoryService.delete(ids);
        return Response.noContent().build();
    }
}
