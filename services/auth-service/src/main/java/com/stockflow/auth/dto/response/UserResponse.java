package com.stockflow.auth.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.stockflow.auth.enums.RoleType;

public record UserResponse(
		UUID id,
		String username,
		String email,
		Set<RoleType> roles,
		boolean enabled,
		Instant createdAt) {
}
