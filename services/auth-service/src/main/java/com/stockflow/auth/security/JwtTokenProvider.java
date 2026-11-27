package com.stockflow.auth.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.stockflow.auth.entity.Role;
import com.stockflow.auth.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	private final RSAKey rsaKey;
	private final JwtEncoder encoder;
	private final JwtDecoder decoder;
	private final String issuer;
	private final Duration accessTokenTtl;

	public JwtTokenProvider(
			@Value("${stockflow.jwt.issuer:stockflow-auth-service}") String issuer,
			@Value("${stockflow.jwt.access-token-ttl:PT15M}") Duration accessTokenTtl,
			@Value("${stockflow.jwt.key-id:stockflow-auth}") String keyId,
			@Value("${stockflow.jwt.public-key:}") String publicKey,
			@Value("${stockflow.jwt.private-key:}") String privateKey) {
		this.issuer = issuer;
		this.accessTokenTtl = accessTokenTtl;
		this.rsaKey = createRsaKey(keyId, publicKey, privateKey);
		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
		this.encoder = new NimbusJwtEncoder(jwkSource);
		this.decoder = createDecoder(rsaKey, issuer);
	}

	public String generateAccessToken(User user) {
		Instant issuedAt = Instant.now();
		List<String> roles = user.getRoles().stream()
				.map(Role::getName)
				.map(Enum::name)
				.sorted()
				.toList();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(user.getUsername())
				.issuedAt(issuedAt)
				.expiresAt(issuedAt.plus(accessTokenTtl))
				.claim("userId", user.getId().toString())
				.claim("roles", roles)
				.build();
		JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
				.keyId(rsaKey.getKeyID())
				.build();
		return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public JwtDecoder jwtDecoder() {
		return decoder;
	}

	public long accessTokenExpiresInSeconds() {
		return accessTokenTtl.toSeconds();
	}

	public Map<String, Object> publicJwkSet() {
		return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
	}

	private JwtDecoder createDecoder(RSAKey key, String expectedIssuer) {
		try {
			NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey())
					.signatureAlgorithm(SignatureAlgorithm.RS256)
					.build();
			jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(expectedIssuer));
			return jwtDecoder;
		}
		catch (com.nimbusds.jose.JOSEException exception) {
			throw new IllegalStateException("Unable to initialize the JWT decoder", exception);
		}
	}

	private RSAKey createRsaKey(String keyId, String publicKey, String privateKey) {
		if (publicKey.isBlank() && privateKey.isBlank()) {
			LOGGER.warn("No JWT key pair configured; generating an ephemeral RSA key pair for this process");
			return generateRsaKey(keyId);
		}
		if (publicKey.isBlank() || privateKey.isBlank()) {
			throw new IllegalStateException("Both JWT_PUBLIC_KEY and JWT_PRIVATE_KEY must be configured together");
		}
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKey parsedPublicKey = (RSAPublicKey) keyFactory.generatePublic(
					new X509EncodedKeySpec(decodeKey(publicKey, "PUBLIC")));
			RSAPrivateKey parsedPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(
					new PKCS8EncodedKeySpec(decodeKey(privateKey, "PRIVATE")));
			return new RSAKey.Builder(parsedPublicKey)
					.privateKey(parsedPrivateKey)
					.keyID(keyId)
					.build();
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to parse the configured RSA JWT key pair", exception);
		}
	}

	private RSAKey generateRsaKey(String keyId) {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();
			return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
					.privateKey((RSAPrivateKey) keyPair.getPrivate())
					.keyID(keyId)
					.build();
		}
		catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("RSA is not available", exception);
		}
	}

	private byte[] decodeKey(String value, String type) {
		String normalized = value
				.replace("-----BEGIN " + type + " KEY-----", "")
				.replace("-----END " + type + " KEY-----", "")
				.replaceAll("\\s", "");
		return Base64.getDecoder().decode(normalized);
	}
}
