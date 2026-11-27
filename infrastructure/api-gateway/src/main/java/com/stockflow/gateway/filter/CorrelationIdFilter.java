package com.stockflow.gateway.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class CorrelationIdFilter extends OncePerRequestFilter {

	public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
	private static final String MDC_KEY = "correlationId";
	private static final Pattern VALID_CORRELATION_ID = Pattern.compile("[A-Za-z0-9._-]{1,128}");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String correlationId = resolveCorrelationId(request.getHeader(CORRELATION_ID_HEADER));
		String previousCorrelationId = MDC.get(MDC_KEY);
		HttpServletRequest wrappedRequest = new CorrelationIdRequestWrapper(request, correlationId);

		MDC.put(MDC_KEY, correlationId);
		response.setHeader(CORRELATION_ID_HEADER, correlationId);
		try {
			filterChain.doFilter(wrappedRequest, response);
		}
		finally {
			if (previousCorrelationId == null) {
				MDC.remove(MDC_KEY);
			}
			else {
				MDC.put(MDC_KEY, previousCorrelationId);
			}
		}
	}

	private String resolveCorrelationId(String candidate) {
		if (candidate != null && VALID_CORRELATION_ID.matcher(candidate).matches()) {
			return candidate;
		}
		return UUID.randomUUID().toString();
	}

	private static final class CorrelationIdRequestWrapper extends HttpServletRequestWrapper {

		private final String correlationId;

		private CorrelationIdRequestWrapper(HttpServletRequest request, String correlationId) {
			super(request);
			this.correlationId = correlationId;
		}

		@Override
		public String getHeader(String name) {
			if (CORRELATION_ID_HEADER.equalsIgnoreCase(name)) {
				return correlationId;
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			if (CORRELATION_ID_HEADER.equalsIgnoreCase(name)) {
				return Collections.enumeration(List.of(correlationId));
			}
			return super.getHeaders(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			Set<String> names = new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
			if (names.stream().noneMatch(CORRELATION_ID_HEADER::equalsIgnoreCase)) {
				names.add(CORRELATION_ID_HEADER);
			}
			return Collections.enumeration(names);
		}
	}

}
