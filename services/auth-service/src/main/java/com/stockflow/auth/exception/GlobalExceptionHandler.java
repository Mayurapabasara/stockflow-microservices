package com.stockflow.auth.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(UserNotFoundException.class)
	ResponseEntity<Map<String, Object>> handleNotFound(UserNotFoundException exception, HttpServletRequest request) {
		return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler({UserAlreadyExistsException.class, DataIntegrityViolationException.class})
	ResponseEntity<Map<String, Object>> handleConflict(Exception exception, HttpServletRequest request) {
		return response(HttpStatus.CONFLICT, "A user with that username or email already exists", request, Map.of());
	}

	@ExceptionHandler(AuthenticationException.class)
	ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException exception, HttpServletRequest request) {
		return response(HttpStatus.UNAUTHORIZED, "Invalid username, email, or password", request, Map.of());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException exception, HttpServletRequest request) {
		return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, Object>> handleValidation(
			MethodArgumentNotValidException exception, HttpServletRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError error : exception.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}
		return response(HttpStatus.BAD_REQUEST, "Request validation failed", request, Map.of("errors", errors));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception, HttpServletRequest request) {
		LOGGER.error("Unexpected authentication service error", exception);
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, Map.of());
	}

	private ResponseEntity<Map<String, Object>> response(
			HttpStatus status, String message, HttpServletRequest request, Map<String, Object> details) {
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
