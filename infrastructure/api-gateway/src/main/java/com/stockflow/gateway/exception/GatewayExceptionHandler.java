package com.stockflow.gateway.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import static com.stockflow.gateway.filter.CorrelationIdFilter.CORRELATION_ID_HEADER;

@RestControllerAdvice
public class GatewayExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	ResponseEntity<Map<String, Object>> handleResponseStatus(
			ResponseStatusException exception, HttpServletRequest request) {
		String message = exception.getReason() == null ? "Gateway request failed" : exception.getReason();
		return ResponseEntity.status(exception.getStatusCode())
				.body(errorBody(exception.getStatusCode().value(), message, request));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception, HttpServletRequest request) {
		LOGGER.error("Unexpected gateway error", exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"An unexpected gateway error occurred", request));
	}

	private Map<String, Object> errorBody(int status, String message, HttpServletRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status);
		body.put("message", message);
		body.put("path", request.getRequestURI());
		body.put("correlationId", request.getHeader(CORRELATION_ID_HEADER));
		return body;
	}

}
