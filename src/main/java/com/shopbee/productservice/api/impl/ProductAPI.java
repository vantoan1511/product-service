package com.shopbee.productservice.api.impl;

import com.shopbee.productservice.api.IProductAPI;
import com.shopbee.productservice.dto.*;
import com.shopbee.productservice.entity.Product;
import com.shopbee.productservice.service.ProductImageService;
import com.shopbee.productservice.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.util.List;

public class ProductAPI implements IProductAPI {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @ConfigProperty(name = "API_KEY")
    private String secretAPIKey;

    @Inject
    public ProductAPI(ProductService productService,
                      ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @Override
    public Response getByCriteria(@BeanParam FilterCriteria filterCriteria,
                                  @BeanParam SortCriteria sortCriteria,
                                  @BeanParam PageRequest pageRequest) {
        return Response.ok(productService.getByCriteria(filterCriteria, sortCriteria, pageRequest)).build();
    }

    @Override
    public Response getBySlug(@PathParam("slug") String slug) {
        productService.increaseView(slug);
        return Response.ok(productService.getBySlug(slug)).build();
    }

    @Override
    public Response create(ProductCreationRequest productCreationRequest, @Context UriInfo uriInfo) {
        Product product = productService.create(productCreationRequest);
        URI uri = uriInfo.getAbsolutePathBuilder().path(product.getSlug()).build();
        return Response.created(uri).entity(product).build();
    }

    @Override
    public Response update(@PathParam("id") Long id, ProductCreationRequest productCreationRequest) {
        productService.update(id, productCreationRequest);
        return Response.ok().build();
    }

    @Override
    public Response updatePartially(String productSlug, UpdatePartialProductRequest updatePartialProductRequest) {
        productService.updatePartially(productSlug, updatePartialProductRequest);
        return Response.ok().build();
    }

    @Override
    public Response addFavourite(String productSlug) {
        return Response.status(Response.Status.CREATED).entity(productService.addFavourite(productSlug)).build();
    }

    @Override
    public Response getFavourites(PageRequest pageRequest) {
        return Response.ok(productService.getFavouritePagedResponse(pageRequest)).build();
    }

    @Override
    public Response updateQuantity(@HeaderParam(value = "API_KEY") String apiKey,
                                   ProductQuantityRequest productQuantityRequest) {
        if (!secretAPIKey.equals(apiKey)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        productService.updateQuantity(productQuantityRequest);
        return Response.ok().build();
    }

    @Override
    public Response delete(List<Long> ids) {
        productService.delete(ids);
        return Response.noContent().build();
    }

    @Override
    public Response getImages(@PathParam("id") Long id) {
        return Response.ok(productImageService.getProductImages(id)).build();
    }

    @Override
    public Response updateImages(@PathParam("id") Long id, List<Long> imageIds) {
        productImageService.updateImages(id, imageIds);
        return Response.ok().build();
    }

    @Override
    public Response setFeaturedImage(@PathParam("id") Long productId, Long imageId) {
        productImageService.setFeaturedImage(productId, imageId);
        return Response.ok().build();
    }

    @Override
    public Response removeImages(@PathParam("id") Long id, List<Long> imageIds) {
        productImageService.removeImages(id, imageIds);
        return Response.noContent().build();
    }

    @Override
    public Response getStatistic() {
        return Response.ok(productService.getProductStatistic()).build();
    }

    @Override
    public Response getRecommendations() {
        return Response.ok(productService.getRecommendations()).build();
    }
}
