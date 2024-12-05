package com.shopbee.productservice.api;

import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.security.constant.Role;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IProductAPI {

    @GET
    Response getByCriteria(@BeanParam FilterCriteria filterCriteria,
                           @BeanParam SortCriteria sortCriteria,
                           @BeanParam PageRequest pageRequest);

    @POST
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response create(ProductCreationRequest productCreationRequest, @Context UriInfo uriInfo);

    @PATCH
    Response updateQuantity(@HeaderParam(value = "API_KEY") String apiKey,
                            ProductQuantityRequest productQuantityRequest);

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response delete(List<Long> ids);

    @GET
    @Path("{slug}")
    Response getBySlug(@PathParam("slug") String slug);

    @PATCH
    @Path("{slug}")
    @Authenticated
    Response updatePartially(@PathParam("slug") String productSlug, @Valid @NotNull UpdatePartialProductRequest updatePartialProductRequest);

    @POST
    @Path("{slug}/favourites")
    @Authenticated
    Response addFavourite(@PathParam("slug") String productSlug);

    @GET
    @Path("favourites")
    @Authenticated
    Response getFavourites(@Valid PageRequest pageRequest);

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response update(@PathParam("id") Long id, ProductCreationRequest productCreationRequest);

    @GET
    @Path("{id}/images")
    Response getImages(@PathParam("id") Long id);

    @PUT
    @Path("{id}/images")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response updateImages(@PathParam("id") Long id, List<Long> imageIds);

    @PATCH
    @Path("{id}/images/{imageId}")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response setFeaturedImage(@PathParam("id") Long productId, Long imageId);

    @DELETE
    @Path("{id}/images")
    @RolesAllowed({Role.ROLE_ADMIN, Role.ROLE_STAFF})
    Response removeImages(@PathParam("id") Long id, List<Long> imageIds);

    @GET
    @Path("statistic")
    Response getStatistic();

    @GET
    @Path("recommendations")
    Response getRecommendations();
}
