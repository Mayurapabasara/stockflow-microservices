package com.stockflow.sales.client;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import com.stockflow.sales.exception.DependencyUnavailableException;
@Component
public class ProductClient {
    private final RestClient client;
    private final boolean enabled;
    public ProductClient(@Value("${stockflow.product.validation-enabled:false}") boolean enabled,
            @Value("${stockflow.product.base-url:http://localhost:8082}") String baseUrl) {
        this.enabled = enabled;
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }
    public void assertExists(UUID id) {
        if (!enabled) return;
        try {
            client.get().uri("/api/products/{id}", id).headers(headers -> {
                if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt)
                    headers.setBearerAuth(jwt.getToken().getTokenValue());
            }).retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) throw new IllegalArgumentException("Product not found");
            throw new DependencyUnavailableException("Product Service validation failed", e);
        } catch (RestClientException e) {
            throw new DependencyUnavailableException("Product Service is unavailable", e);
        }
    }
}
