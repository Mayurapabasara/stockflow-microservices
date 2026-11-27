package com.stockflow.auth.service.impl;

import java.util.Locale;
import java.util.UUID;

import com.stockflow.auth.dto.response.UserResponse;
import com.stockflow.auth.entity.User;
import com.stockflow.auth.exception.UserNotFoundException;
import com.stockflow.auth.mapper.UserMapper;
import com.stockflow.auth.repository.UserRepository;
import com.stockflow.auth.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public UserResponse getCurrentUser(String username) {
		User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT))
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		return userMapper.toResponse(user);
	}

	@Override
	public UserResponse getUser(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		return userMapper.toResponse(user);
	}
}
