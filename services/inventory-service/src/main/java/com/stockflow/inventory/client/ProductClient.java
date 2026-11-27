package com.stockflow.inventory.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ProductClient {

	private final RestClient restClient;
	private final boolean validationEnabled;

	public ProductClient(@Value("${stockflow.product.validation-enabled:false}") boolean validationEnabled,
			@Value("${stockflow.product.base-url:http://localhost:8082}") String productServiceUrl) {
		this.restClient = RestClient.builder().baseUrl(productServiceUrl).build();
		this.validationEnabled = validationEnabled;
	}

	public void assertProductExists(UUID productId) {
		if (!validationEnabled) {
			return;
		}
		try {
			restClient.get()
					.uri("/api/products/{productId}", productId)
					.retrieve()
					.toBodilessEntity();
		}
		catch (RestClientResponseException exception) {
			if (exception.getStatusCode().value() == 404) {
				throw new IllegalArgumentException("Product not found");
			}
			throw new IllegalStateException("Product service returned an unexpected response", exception);
		}
		catch (RestClientException exception) {
			throw new IllegalStateException("Product service is unavailable", exception);
		}
	}
}
