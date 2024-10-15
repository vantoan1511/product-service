package com.shopbee.productservice.service.user;

import com.shopbee.productservice.exception.ProductServiceException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final String BEARER = "Bearer ";

    @RestClient
    UserResource userResource;

    @Inject
    JsonWebToken jsonWebToken;

    public User getByUsername(String username) {
        try (Response response = userResource.search(username, getBearerToken())) {
            PagedResponse pagedResponse = response.readEntity(PagedResponse.class);
            return Optional.ofNullable(pagedResponse.getItems())
                    .map(List::getFirst)
                    .orElseThrow(() -> new ProductServiceException("User not found", Response.Status.NOT_FOUND));
        } catch (ClientWebApplicationException e) {
            LOG.error("Get user by username failed: {}", e.getLocalizedMessage());
            throw new ProductServiceException("An error occurred when calling user service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public User getById(Long id) {
        try (Response response = userResource.getById(id, getBearerToken())) {
            return response.readEntity(User.class);
        } catch (ClientWebApplicationException e) {
            LOG.error("Get user by id failed: {}", e.getLocalizedMessage());
            throw new ProductServiceException("An error occurred when calling user service", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private String getBearerToken() {
        return BEARER + getRawToken();
    }

    private String getRawToken() {
        return jsonWebToken.getRawToken();
    }
}
