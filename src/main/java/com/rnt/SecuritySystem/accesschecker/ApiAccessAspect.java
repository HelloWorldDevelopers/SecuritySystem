package com.rnt.SecuritySystem.accesschecker;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnt.SecuritySystem.dto.AccessDTO;
import com.rnt.SecuritySystem.exception.AccessDeniedException;
import com.rnt.SecuritySystem.helper.JwtHelper;
import com.rnt.SecuritySystem.util.AESUtil;

import io.jsonwebtoken.Claims;

@Aspect
@Component
public class ApiAccessAspect {

	@Value("${AES.KEY}")
	private String aesKey;

	@Autowired
	JwtHelper jwtUtil;

	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer";

	@Before("@annotation(checkApiAccess)")
	public void checkAccess(JoinPoint joinPoint, CheckApiAccess checkApiAccess) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
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
			Claims userAccess = jwtUtil.extractAllClaims(requestTokenHeader);
			Object accessObject = userAccess.get("access");
			ObjectMapper mapper = new ObjectMapper();
			List<AccessDTO> accessList = mapper.convertValue(accessObject, new TypeReference<List<AccessDTO>>() {
			});

			if (!accessList.isEmpty() && accessList.size() > 0) {
				boolean isAdmin = accessList.stream().anyMatch(e -> e.getUseCase().equalsIgnoreCase("admin"));
				if (!isAdmin) {
					String trimmedPath = request.getServletPath().replaceFirst("/api/v1/", "");
					ApiEndpointsDetails apiEndpointsDetails = ApiEndpoints.getAllApiEndpoints().get(trimmedPath);
					if (apiEndpointsDetails == null) {
						throw new AccessDeniedException("Access denied: No permission for use case ");
					}
					boolean isModuleAccess = accessList.stream()
							.anyMatch(e -> e.getUseCase().equalsIgnoreCase(apiEndpointsDetails.getUserCase()));
					if (!isModuleAccess) {
						throw new AccessDeniedException("Access denied: No permission for use case ");
					}
				}

			} else {
				throw new AccessDeniedException("Access denied: No permission for use case ");
			}

		}
	}
}
