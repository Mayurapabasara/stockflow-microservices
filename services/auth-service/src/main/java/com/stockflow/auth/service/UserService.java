package com.stockflow.auth.service;

import java.util.UUID;

import com.stockflow.auth.dto.response.UserResponse;

public interface UserService {

	UserResponse getCurrentUser(String username);

	UserResponse getUser(UUID id);
}
