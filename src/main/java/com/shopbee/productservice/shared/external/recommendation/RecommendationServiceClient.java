package com.shopbee.productservice.shared.external.recommendation;

import com.shopbee.productservice.shared.Constant;
import com.shopbee.productservice.shared.ExternalServiceExceptionMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("evaluate")
@RegisterRestClient(configKey = Constant.RECOMMENDATION_SERVICE_CLIENT)
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(ExternalServiceExceptionMapper.class)
public interface RecommendationServiceClient {

    @POST
    GetEvaluateResponse evaluate(GetRecommendedProductsRequest getRecommendedProductsRequest);
}