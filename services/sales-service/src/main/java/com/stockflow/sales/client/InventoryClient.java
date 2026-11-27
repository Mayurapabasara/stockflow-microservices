package com.stockflow.sales.client;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stockflow.sales.exception.DependencyUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.*;
@Component
public class InventoryClient {
    private final RestClient client;
    private final boolean enabled;
    public InventoryClient(@Value("${stockflow.inventory.validation-enabled:false}") boolean enabled,
            @Value("${stockflow.inventory.base-url:http://localhost:8083}") String baseUrl) {
        this.enabled = enabled; this.client = RestClient.builder().baseUrl(baseUrl).build();
    }
    public void assertAvailable(UUID warehouseId, UUID productId, long quantity) {
        if (!enabled) return;
        try {
            var stock = client.get().uri("/api/inventory/{warehouseId}/products/{productId}", warehouseId, productId)
                .headers(headers -> {
                    if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt)
                        headers.setBearerAuth(jwt.getToken().getTokenValue());
                }).retrieve().body(StockBalance.class);
            if (stock == null || stock.availableQuantity() == null)
                throw new DependencyUnavailableException("Inventory Service returned no balance", null);
            if (stock.availableQuantity() < quantity) throw new IllegalStateException("Insufficient available stock");
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) throw new IllegalArgumentException("Inventory record not found");
            throw new DependencyUnavailableException("Inventory Service validation failed", e);
        } catch (RestClientException e) { throw new DependencyUnavailableException("Inventory Service is unavailable", e); }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StockBalance(Long availableQuantity) {}
}
