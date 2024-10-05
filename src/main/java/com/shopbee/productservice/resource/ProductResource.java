package com.shopbee.productservice.resource;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.ProductQuantityRequest;
import com.shopbee.productservice.dto.ProductRequest;
import com.shopbee.productservice.dto.SortCriteria;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.security.constant.Role;
import com.shopbee.productservice.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProductResource.class);

    @ConfigProperty(name = "API_KEY")
    private String secretAPIKey;

    ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public Response getByCriteria(@BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(productService.getByCriteria(sortCriteria, pageRequest)).build();
    }

    @GET
    @Path("{slug}")
    public Response getBySlug(@PathParam("slug") String slug) {
        productService.increaseView(slug);
        return Response.ok(productService.getBySlug(slug)).build();
    }

    @POST
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response create(ProductRequest productRequest, @Context UriInfo uriInfo) {
        Product product = productService.create(productRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(product.getSlug()).build();
        return Response.created(uri).entity(product).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({Role.ROLE_ADMIN})
    public Response update(@PathParam("id") Long id, ProductRequest productRequest) {
        productService.update(id, productRequest);
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

}
