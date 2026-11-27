package com.stockflow.auth.controller;

import java.util.Map;

import com.stockflow.auth.dto.request.LoginRequest;
import com.stockflow.auth.dto.request.RefreshTokenRequest;
import com.stockflow.auth.dto.request.RegisterRequest;
import com.stockflow.auth.dto.response.AuthResponse;
import com.stockflow.auth.security.JwtTokenProvider;
import com.stockflow.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private final AuthService authService;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
		this.authService = authService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/api/auth/register")
	ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/api/auth/login")
	AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/api/auth/refresh")
	AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return authService.refresh(request);
	}

	@PostMapping("/api/auth/logout")
	ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/.well-known/jwks.json")
	Map<String, Object> jwks() {
		return jwtTokenProvider.publicJwkSet();
	}
}
