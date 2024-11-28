package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.security.constant.Role;
import com.shopbee.productservice.service.ProductImageService;
import com.shopbee.productservice.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    @ConfigProperty(name = "API_KEY")
    private String secretAPIKey;

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductResource(ProductService productService,
                           ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @GET
    public Response getByCriteria(@BeanParam FilterCriteria filterCriteria,
                                  @BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(productService.getByCriteria(filterCriteria, sortCriteria, pageRequest)).build();
    }

    @GET
    @Path("{slug}")
    public Response getBySlug(@PathParam("slug") String slug) {
        productService.increaseView(slug);
        return Response.ok(productService.getBySlug(slug)).build();
    }

    @POST
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response create(ProductCreationRequest productCreationRequest, @Context UriInfo uriInfo) {
        Product product = productService.create(productCreationRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(product.getSlug()).build();
        return Response.created(uri).entity(product).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response update(@PathParam("id") Long id, ProductCreationRequest productCreationRequest) {
        productService.update(id, productCreationRequest);
        return Response.ok().build();
    }

    @PATCH
    public Response updateQuantity(@HeaderParam(value = "API_KEY") String apiKey,
                                   ProductQuantityRequest productQuantityRequest) {
        if (!secretAPIKey.equals(apiKey)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        productService.updateQuantity(productQuantityRequest);
        return Response.ok().build();
    }

    @DELETE
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response delete(List<Long> ids) {
        productService.delete(ids);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/images")
    public Response getImages(@PathParam("id") Long id) {
        return Response.ok(productImageService.getProductImages(id)).build();
    }

    @PUT
    @Path("{id}/images")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response updateImages(@PathParam("id") Long id, List<Long> imageIds) {
        productImageService.updateImages(id, imageIds);
        return Response.ok().build();
    }

    @PATCH
    @Path("{id}/images/{imageId}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response setFeaturedImage(@PathParam("id") Long productId, Long imageId) {
        productImageService.setFeaturedImage(productId, imageId);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}/images")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response removeImages(@PathParam("id") Long id, List<Long> imageIds) {
        productImageService.removeImages(id, imageIds);
        return Response.noContent().build();
    }
}
