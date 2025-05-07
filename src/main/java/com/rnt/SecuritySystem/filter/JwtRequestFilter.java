package com.rnt.SecuritySystem.filter;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.rnt.SecuritySystem.entity.TempUserBlock;
import com.rnt.SecuritySystem.helper.JwtHelper;
import com.rnt.SecuritySystem.repo.TempUserBlockRepo;
import com.rnt.SecuritySystem.securityconfig.UserDetail;
import com.rnt.SecuritySystem.service.CustomeUserDetailsService;
import com.rnt.SecuritySystem.util.AESUtil;
import com.rnt.SecuritySystem.util.AuthenticationUtil;
import com.rnt.SecuritySystem.util.CheckMaliciousPalyload;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private TempUserBlockRepo tempUserBlockRepo;

	@Autowired
	private JWTAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JwtHelper jwtUtil;

	@Autowired
	private CustomeUserDetailsService customeUserDetailsService;

	@Value("${AES.KEY}")
	private String aesKey;

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest wrappedRequest = request;

		try {
			String ip = request.getRemoteAddr();
			TempUserBlock findByIpAddress = tempUserBlockRepo.findByIpAddress(ip);

			if (findByIpAddress != null && findByIpAddress.getAttempt() == 5) {
				if (Duration.between(findByIpAddress.getBlockedAt(), LocalDateTime.now()).toHours() >= 24) {
					tempUserBlockRepo.deleteByIpAddress(ip); // Unblock
				} else {
					throw new IllegalArgumentException(
							"Your account has been blocked. Please contact support. Try again after 24 hours.");
				}
			}

			boolean isModifyingRequest = !"GET".equalsIgnoreCase(request.getMethod())
					&& !"DELETE".equalsIgnoreCase(request.getMethod());

			if (isModifyingRequest) {
				wrappedRequest = new CachedBodyHttpServletRequest(request);
				if (CheckMaliciousPalyload
						.checkValue(((CachedBodyHttpServletRequest) wrappedRequest).getBodyString())) {
					throw new IllegalArgumentException("Malicious payload detected in request body.");
				}
			}

			if (AuthenticationUtil.ALLOW_URL.test(request.getServletPath())) {
				filterChain.doFilter(wrappedRequest, response);
				return;
			}
			
			

			String requestTokenHeader = request.getHeader(AUTHORIZATION);
			if (Objects.isNull(requestTokenHeader)) {
				throw new MissingServletRequestPartException("AUTHORIZATION Header is missing");
			}

			if (requestTokenHeader.startsWith(BEARER)) {
				requestTokenHeader = requestTokenHeader.substring(7);
				try {
					requestTokenHeader = AESUtil.decrypt(requestTokenHeader, aesKey);
				} catch (Exception e) {
					throw new IllegalArgumentException("Access denied due to token mismatch. Please log in again.");
				}

				String userName = jwtUtil.extractUsername(requestTokenHeader);
				String tokenIp = jwtUtil.extractIpAddress(requestTokenHeader);
				
				

				if (!request.getRemoteAddr().equals(tokenIp)) {
					throw new IllegalArgumentException("User mismatch detected. Please log in again to continue.");
				}

				UserDetail loadUserByUsername = customeUserDetailsService.loadUserByUsername(userName);
				if (Boolean.TRUE.equals(jwtUtil.validateToken(requestTokenHeader, loadUserByUsername))) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							loadUserByUsername, null, loadUserByUsername.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(loadUserByUsername);
					getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}

			filterChain.doFilter(wrappedRequest, response);

		} catch (ExpiredJwtException e) {
			authenticationEntryPoint.commence(request, response,
					new InsufficientAuthenticationException("Token Expired", e));
		} catch (IllegalArgumentException e) {
			authenticationEntryPoint.commence(request, response, new IllegalArgumentException(e.getMessage(), e));
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		}
	}
}
