package com.stockflow.auth.mapper;

import java.util.EnumSet;
import java.util.stream.Collectors;

import com.stockflow.auth.dto.response.UserResponse;
import com.stockflow.auth.entity.Role;
import com.stockflow.auth.entity.User;
import com.stockflow.auth.enums.RoleType;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public UserResponse toResponse(User user) {
		EnumSet<RoleType> roles = user.getRoles().stream()
				.map(Role::getName)
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(RoleType.class)));
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				roles,
				user.isEnabled(),
				user.getCreatedAt());
	}
}
