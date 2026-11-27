package com.stockflow.auth.controller;

import java.util.UUID;

import com.stockflow.auth.dto.response.UserResponse;
import com.stockflow.auth.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	UserResponse currentUser(Authentication authentication) {
		return userService.getCurrentUser(authentication.getName());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	UserResponse user(@PathVariable UUID id) {
		return userService.getUser(id);
	}
}
