package com.stockflow.supplier.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(SupplierNotFoundException.class)
	ResponseEntity<Map<String, Object>> notFound(SupplierNotFoundException exception, HttpServletRequest request) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler({DuplicateSupplierCodeException.class, DataIntegrityViolationException.class})
	ResponseEntity<Map<String, Object>> conflict(Exception exception, HttpServletRequest request) {
		return response(HttpStatus.CONFLICT, "A supplier with that unique value already exists", request, Map.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}
		return response(HttpStatus.BAD_REQUEST, "Request validation failed", request, Map.of("errors", errors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<Map<String, Object>> malformedRequest(HttpMessageNotReadableException exception, HttpServletRequest request) {
		return response(HttpStatus.BAD_REQUEST, "Malformed request payload", request, Map.of());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Map<String, Object>> unexpected(Exception exception, HttpServletRequest request) {
		LOGGER.error("Unexpected supplier service error", exception);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, Map.of());
	}

	private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message,
			HttpServletRequest request, Map<String, Object> details) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", request.getRequestURI());
		body.putAll(details);
		return ResponseEntity.status(status).body(body);
	}
}
