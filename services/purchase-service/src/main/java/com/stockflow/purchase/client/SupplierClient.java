package com.stockflow.purchase.client;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import com.stockflow.purchase.exception.DependencyUnavailableException;
@Component
public class SupplierClient {
    private final RestClient client;
    private final boolean enabled;
    public SupplierClient(@Value("${stockflow.supplier.validation-enabled:false}") boolean enabled,
            @Value("${stockflow.supplier.base-url:http://localhost:8084}") String baseUrl) {
        this.enabled = enabled;
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }
    public void assertExists(UUID id) {
        if (!enabled) return;
        try {
            client.get().uri("/api/suppliers/{id}", id).headers(headers -> {
                if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt)
                    headers.setBearerAuth(jwt.getToken().getTokenValue());
            }).retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) throw new IllegalArgumentException("Supplier not found");
            throw new DependencyUnavailableException("Supplier Service validation failed", e);
        } catch (RestClientException e) {
            throw new DependencyUnavailableException("Supplier Service is unavailable", e);
        }
    }
}
