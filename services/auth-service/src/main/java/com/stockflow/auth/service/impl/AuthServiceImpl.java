package com.stockflow.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

import com.stockflow.auth.dto.request.LoginRequest;
import com.stockflow.auth.dto.request.RefreshTokenRequest;
import com.stockflow.auth.dto.request.RegisterRequest;
import com.stockflow.auth.dto.response.AuthResponse;
import com.stockflow.auth.entity.RefreshToken;
import com.stockflow.auth.entity.Role;
import com.stockflow.auth.entity.User;
import com.stockflow.auth.enums.RoleType;
import com.stockflow.auth.exception.UserAlreadyExistsException;
import com.stockflow.auth.exception.UserNotFoundException;
import com.stockflow.auth.mapper.UserMapper;
import com.stockflow.auth.repository.RefreshTokenRepository;
import com.stockflow.auth.repository.RoleRepository;
import com.stockflow.auth.repository.UserRepository;
import com.stockflow.auth.security.JwtTokenProvider;
import com.stockflow.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserMapper userMapper;
	private final Duration refreshTokenTtl;

	public AuthServiceImpl(
			UserRepository userRepository,
			RoleRepository roleRepository,
			RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider,
			UserMapper userMapper,
			@Value("${stockflow.jwt.refresh-token-ttl:P7D}") Duration refreshTokenTtl) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userMapper = userMapper;
		this.refreshTokenTtl = refreshTokenTtl;
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		String username = request.username().trim().toLowerCase(Locale.ROOT);
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		if (userRepository.existsByUsername(username)) {
			throw new UserAlreadyExistsException("Username is already registered");
		}
		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("Email is already registered");
		}

		Role userRole = roleRepository.findByName(RoleType.USER)
				.orElseThrow(() -> new IllegalStateException("Default USER role has not been initialized"));
		User user = new User(username, email, passwordEncoder.encode(request.password()));
		user.getRoles().add(userRole);
		userRepository.save(user);
		return issueTokens(user);
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		String identity = request.usernameOrEmail().trim().toLowerCase(Locale.ROOT);
		String username = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(identity, request.password())).getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		return issueTokens(user);
	}

	@Override
	public AuthResponse refresh(RefreshTokenRequest request) {
		RefreshToken existingToken = refreshTokenRepository.findByTokenHash(hashToken(request.refreshToken()))
				.orElseThrow(() -> new IllegalArgumentException("Refresh token is invalid"));
		if (existingToken.isRevoked() || !existingToken.getExpiresAt().isAfter(Instant.now())) {
			throw new IllegalArgumentException("Refresh token is expired or revoked");
		}
		existingToken.setRevoked(true);
		return issueTokens(existingToken.getUser());
	}

	@Override
	public void logout(RefreshTokenRequest request) {
		refreshTokenRepository.findByTokenHash(hashToken(request.refreshToken()))
				.ifPresent(token -> token.setRevoked(true));
	}

	private AuthResponse issueTokens(User user) {
		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshTokenValue = generateRefreshToken();
		RefreshToken refreshToken = new RefreshToken(
				hashToken(refreshTokenValue), Instant.now().plus(refreshTokenTtl), user);
		refreshTokenRepository.save(refreshToken);
		return new AuthResponse(
				accessToken,
				refreshTokenValue,
				jwtTokenProvider.accessTokenExpiresInSeconds(),
				userMapper.toResponse(user));
	}

	private String generateRefreshToken() {
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hashToken(String token) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(token.getBytes(StandardCharsets.UTF_8));
			return java.util.HexFormat.of().formatHex(digest);
		}
		catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}
}
