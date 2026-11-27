package com.stockflow.auth.security;

import java.util.Locale;

import com.stockflow.auth.entity.Role;
import com.stockflow.auth.entity.User;
import com.stockflow.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		String identity = usernameOrEmail.trim().toLowerCase(Locale.ROOT);
		User user = userRepository.findByUsernameOrEmail(identity, identity)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		String[] authorities = user.getRoles().stream()
				.map(Role::getName)
				.map(role -> "ROLE_" + role.name())
				.toArray(String[]::new);
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPasswordHash())
				.authorities(authorities)
				.disabled(!user.isEnabled())
				.build();
	}
}
