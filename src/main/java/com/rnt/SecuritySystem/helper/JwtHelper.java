package com.rnt.SecuritySystem.helper;
 
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.rnt.SecuritySystem.dto.AccessDTO;
import com.rnt.SecuritySystem.entity.EmployeeMaster;
import com.rnt.SecuritySystem.repo.UserInfoRepo;
import com.rnt.SecuritySystem.service.AccessService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtHelper {

	@Value("${jwt.secret}")
	private String secret;
	public static final long JWT_ACCESS_TOKEN_VALIDITY = 1 * 60 * 60 * 1000; // 1 hours in milliseconds
	public static final long JWT_REFRESH_TOKEN_VALIDITY = 2 * 60 * 60 * 1000; // 2 hours in milliseconds

	@Autowired
	AccessService accessService;

	@Autowired
	UserInfoRepo userInfoRepo;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		Date extractClaim = extractClaim(token, Claims::getExpiration);
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails,String ip) throws Exception {
		System.out.println(secret);
		Map<String, Object> claims = new HashMap<>();
		EmployeeMaster employeeByUserId = userInfoRepo.findByEmail(userDetails.getUsername()).orElse(null);
 
		  List<AccessDTO> accessDataByUserId = accessService.getAccessDataByUserId(Long.valueOf(employeeByUserId.getStaffId()));
		claims.put("id", employeeByUserId.getStaffId());
 		claims.put("email", employeeByUserId.getEmail());
 		claims.put("createdTime", LocalDateTime.now().toString());
 		claims.put("ipAddress",ip);
 		claims.put("access", accessDataByUserId);
		return createToken(claims, userDetails.getUsername());
	}
	
	public String generateRefreshToken(UserDetails userDetails,String ip) throws Exception {
		System.out.println(secret);
		Map<String, Object> claims = new HashMap<>();
		EmployeeMaster employeeByUserId = userInfoRepo.findByEmail(userDetails.getUsername()).orElse(null);
  		claims.put("email", employeeByUserId.getEmail());
 		claims.put("createdTime", LocalDateTime.now().toString());
 		claims.put("ipAddress",ip);
		return createRefreshToken(claims, userDetails.getUsername());
	}
	
	 public String extractIpAddress(String token) {
	        Claims claims = Jwts.parser()
	                            .setSigningKey(secret)
	                            .parseClaimsJws(token)
	                            .getBody();
	        return claims.get("ipAddress", String.class);
	    }
	 
	 public String extractUserAccess(String token) {
	        Claims claims = Jwts.parser()
	                            .setSigningKey(secret)
	                            .parseClaimsJws(token)
	                            .getBody();
	        return claims.get("access", String.class);
	    }
	 
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_VALIDITY))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}
	
	private String createRefreshToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

 

}
