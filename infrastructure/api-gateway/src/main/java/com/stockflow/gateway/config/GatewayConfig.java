package com.stockflow.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayConfig {

	@Bean
	RouterFunction<ServerResponse> authServiceRoute() {
		return serviceRoute("auth-service", "/api/auth/**", "auth-service");
	}

	@Bean
	RouterFunction<ServerResponse> productServiceRoute() {
		return serviceRoute("product-service", "/api/products/**", "product-service");
	}

	@Bean
	RouterFunction<ServerResponse> inventoryServiceRoute() {
		return serviceRoute("inventory-service", "/api/inventory/**", "inventory-service");
	}

	@Bean
	RouterFunction<ServerResponse> salesServiceRoute() {
		return serviceRoute("sales-service", "/api/sales/**", "sales-service");
	}

	@Bean
	RouterFunction<ServerResponse> purchaseServiceRoute() {
		return serviceRoute("purchase-service", "/api/purchases/**", "purchase-service");
	}

	@Bean
	RouterFunction<ServerResponse> supplierServiceRoute() {
		return serviceRoute("supplier-service", "/api/suppliers/**", "supplier-service");
	}

	@Bean
	RouterFunction<ServerResponse> reportingServiceRoute() {
		return serviceRoute("reporting-service", "/api/reports/**", "reporting-service");
	}

	@Bean
	RouterFunction<ServerResponse> notificationServiceRoute() {
		return serviceRoute("notification-service", "/api/notifications/**", "notification-service");
	}

	private RouterFunction<ServerResponse> serviceRoute(String routeId, String path, String serviceId) {
		return route(routeId)
				.route(RequestPredicates.path(path), http())
				.filter(lb(serviceId))
				.build();
	}

}
