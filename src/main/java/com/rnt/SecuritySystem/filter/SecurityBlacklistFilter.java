//package com.rnt.SecuritySystem.filter;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Set;
//import java.util.regex.Pattern;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.rnt.SecuritySystem.entity.TempUserBlock;
//import com.rnt.SecuritySystem.exception.AccountBlockedException;
//import com.rnt.SecuritySystem.exception.MaliciousPayloadException;
//import com.rnt.SecuritySystem.repo.TempUserBlockRepo;
//
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this filter runs before all others
//public class SecurityBlacklistFilter extends OncePerRequestFilter {
//
//	// Common SQL Injection patterns (keywords and symbols)
//	private static final Set<String> SQL_BLACKLIST = Set.of("--", "/*", "*/", "xp_", "exec ", "sp_", "sysobjects",
//			"union select", "information_schema", "waitfor delay", "drop table", "truncate table");
//
//	// Common Cross-site Scripting (XSS) patterns
//	private static final Set<String> XSS_BLACKLIST = Set.of("<script>", "</script>", "javascript:", "onload=",
//			"onerror=", "onclick=", "document.cookie", "eval(", "alert(");
//
//	// Common path traversal or command injection patterns
//	private static final Set<String> PATH_BLACKLIST = Set.of("../", "~/", "/etc/passwd", "cmd.exe", "bash -c", "|", "&",
//			"$(");
//
//	// Regular expression for detecting suspicious SQL-like patterns
//	// (case-insensitive)
//	private static final Pattern SQL_REGEX = Pattern.compile(
//			"(?i)(\\b(select|insert|update|delete|drop|alter|create|rename|truncate|backup)\\b|\\b(and|or)\\b.+\\b(like|=|>|<)\\b)");
//
//	@Autowired
//	TempUserBlockRepo tempUserBlockRepo;
//
//	@Autowired
//	JWTAuthenticationEntryPoint authenticationEntryPoint;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//			throws ServletException, IOException {
//		try {
//
//			String ip = request.getRemoteAddr();
//			TempUserBlock findByIpAddress = tempUserBlockRepo.findByIpAddress(ip);
//
//			if (findByIpAddress != null && findByIpAddress.getAttempt() == 5) {
//				if (Duration.between(findByIpAddress.getBlockedAt(), LocalDateTime.now()).toHours() > 24) {
//					tempUserBlockRepo.deleteByIpAddress(ip); // Unblock
//				} else {
//					throw new AccountBlockedException(
//							"Your account has been blocked. Please contact support. from this device at 24 hours");
//				}
//			}
//
//			if (!request.getMethod().equalsIgnoreCase("GET") && !request.getMethod().equalsIgnoreCase("DELETE")) {
//				CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
//				if (checkValue(wrappedRequest.getBodyString())) {
//					throw new MaliciousPayloadException("Malicious payload detected in request body.");
//				}
//				chain.doFilter(wrappedRequest, response);
//			} else {
//				chain.doFilter(request, response);
//			}
//
//		} catch (AccountBlockedException e) {
//
//			authenticationEntryPoint.commence(request, response, new AccountBlockedException(
//					"Your account has been blocked. Please contact support. from this device at 24 hours"));
//
//		} catch (MaliciousPayloadException e) {
//			request.setAttribute("check", "MaliciousPayload");
//			authenticationEntryPoint.commence(request, response,
//					new MaliciousPayloadException("Malicious payload detected in request body."));
//		} catch (Exception e) {
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
//		}
//
//	}
//
//	private boolean checkValue(String value) {
//		if (value == null)
//			return false;
//		String lowerValue = value.toLowerCase();
//		return SQL_BLACKLIST.stream().anyMatch(lowerValue::contains) || // SQL blacklist
//				SQL_REGEX.matcher(lowerValue).find() || // Regex match for SQL-like logic
//				XSS_BLACKLIST.stream().anyMatch(lowerValue::contains) || // XSS
//				PATH_BLACKLIST.stream().anyMatch(lowerValue::contains); // Command/path injection
//	}
//
//}
