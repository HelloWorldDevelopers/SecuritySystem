package com.rnt.SecuritySystem.controller;

import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rnt.SecuritySystem.dto.Credentials;
import com.rnt.SecuritySystem.dto.TokenBody;
import com.rnt.SecuritySystem.dto.TokenResponseBody;
import com.rnt.SecuritySystem.exception.CustomeException;
import com.rnt.SecuritySystem.helper.JwtHelper;
import com.rnt.SecuritySystem.repo.UserInfoRepo;
import com.rnt.SecuritySystem.securityconfig.UserDetail;
import com.rnt.SecuritySystem.service.CustomeUserDetailsService;
import com.rnt.SecuritySystem.util.AESUtil;

@RestController
@RequestMapping("/api/v1/auth/")
public class LoginController {

	public static HttpServletRequest re;

	@Autowired
	UserInfoRepo userInfoRepo;
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	CustomeUserDetailsService customeUserDetailsService;

	@Autowired
	JwtHelper jwtUtil;

	@Value("${AES.KEY}")
	private String aesKey;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	
	@PostMapping("parse")
	public ResponseEntity<?> parseToken(@RequestBody String token, HttpSession session, HttpServletRequest request) {
		return null;

	}

	@PostMapping("token")
	public ResponseEntity<TokenResponseBody> crateToken(HttpServletResponse res, HttpServletRequest request,
			@RequestBody Credentials credentials, HttpSession session) throws Exception {
 		Instant start = Instant.now();
		if (credentials == null) {
			return ResponseEntity.ok(TokenResponseBody.builder().message("Some fields or Empty")
					.httpStatus(HttpStatus.BAD_REQUEST).durationMs(Duration.between(start, Instant.now()).toMillis())
					.success(Boolean.FALSE).build());
		}
		credentials.setUserName(AESUtil.decrypt(credentials.getUserName(), aesKey));
		credentials.setPassword(AESUtil.decrypt(credentials.getPassword(), aesKey));
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(credentials.getUserName(), credentials.getPassword()));
			UserDetail userDetails = this.customeUserDetailsService.loadUserByUsername(credentials.getUserName());
			String accessToken = this.jwtUtil.generateToken(userDetails, request.getRemoteAddr());
			String refreshToken = this.jwtUtil.generateRefreshToken(userDetails, request.getRemoteAddr());

			return ResponseEntity
					.ok(TokenResponseBody.builder().message("Login successful. Token generated successfully.")
							.httpStatus(HttpStatus.OK).accessToken(AESUtil.encrypt(accessToken, aesKey))
							.refreshToken(AESUtil.encrypt(refreshToken, aesKey)).success(Boolean.TRUE)
							.durationMs(Duration.between(start, Instant.now()).toMillis()).build());
		} catch (Exception e) {
			logger.error("Login failed for " + credentials.toString() + " and for IP address: " + request.getRemoteAddr(),e);
			throw new CustomeException(e.getMessage(), e, request);
		}
	}

	@PostMapping("refresh")
	public ResponseEntity<TokenResponseBody> genrateNewUsingAccessToken(@RequestBody TokenBody token,
			HttpSession session, HttpServletRequest request) throws CustomeException {

		Instant start = Instant.now();
		try {

			String existingRefreshToken = AESUtil.decrypt(token.getRefreshToken(), aesKey);
			Boolean tokenExpired = jwtUtil.isTokenExpired(existingRefreshToken);
			if (Boolean.FALSE.equals(tokenExpired)) {

				String emailAsAUserName = jwtUtil.extractUsername(existingRefreshToken);
				UserDetail userDetails = this.customeUserDetailsService.loadUserByUsername(emailAsAUserName);

				String accessToken = this.jwtUtil.generateToken(userDetails, request.getRemoteAddr());
				String refreshToken = this.jwtUtil.generateRefreshToken(userDetails, request.getRemoteAddr());

				return ResponseEntity.ok(TokenResponseBody.builder().message("New access token generated successfully")
						.httpStatus(HttpStatus.OK).accessToken(AESUtil.encrypt(accessToken, aesKey))
						.refreshToken(AESUtil.encrypt(refreshToken, aesKey)).success(Boolean.TRUE)
						.durationMs(Duration.between(start, Instant.now()).toMillis()).build());
			} else {
				return ResponseEntity
						.ok(TokenResponseBody.builder().message("Refresh token is expired. Please log in again.")
								.httpStatus(HttpStatus.OK).accessToken(null).refreshToken(null).success(Boolean.TRUE)
								.durationMs(Duration.between(start, Instant.now()).toMillis()).build());
			}
		} catch (Exception e) {
			logger.error("Unexpected error during genrate new access token  for IP address: " + request.getRemoteAddr(), e);
			throw new CustomeException(e.getMessage(), e, request);
		} 

	}

	@GetMapping("/csrf")
	public CsrfToken getCsrfToken(HttpServletRequest request) {
		return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
	}

}
