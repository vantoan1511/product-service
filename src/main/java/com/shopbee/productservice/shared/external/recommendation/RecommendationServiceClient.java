package com.shopbee.productservice.shared.external.recommendation;

import com.shopbee.productservice.shared.Constant;
import com.shopbee.productservice.shared.ExternalServiceExceptionMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("recommendations")
@RegisterRestClient(configKey = Constant.RECOMMENDATION_SERVICE_CLIENT)
@RegisterClientHeaders
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(ExternalServiceExceptionMapper.class)
public interface RecommendationServiceClient {

    @POST
    GetRecommendationResponse getRecommendations(GetRecommendedProductsRequest getRecommendedProductsRequest);
}
