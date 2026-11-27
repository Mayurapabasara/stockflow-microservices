package com.stockflow.auth.service;

import com.stockflow.auth.dto.request.LoginRequest;
import com.stockflow.auth.dto.request.RefreshTokenRequest;
import com.stockflow.auth.dto.request.RegisterRequest;
import com.stockflow.auth.dto.response.AuthResponse;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	AuthResponse refresh(RefreshTokenRequest request);

	void logout(RefreshTokenRequest request);
}
