package com.shopbee.productservice.shared.external.review;

import com.shopbee.productservice.dto.PageRequest;
import com.shopbee.productservice.dto.PagedResponse;
import com.shopbee.productservice.shared.Constant;
import com.shopbee.productservice.shared.ExternalServiceExceptionMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("reviews")
@RegisterRestClient(configKey = Constant.REVIEW_SERVICE_CLIENT)
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(ExternalServiceExceptionMapper.class)
public interface ReviewServiceClient {

    @GET
    PagedResponse<Review> getByCriteria(@Valid FilterCriteria filterCriteria,
                                        @Valid PageRequest pageRequest,
                                        @Valid SortCriteria sortCriteria);

    @GET
    @Path("statistic")
    ReviewStatistic getStatistic(@QueryParam("productSlug") String productSlug);
}
