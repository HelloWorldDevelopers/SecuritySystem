package com.rnt.SecuritySystem.exception;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.IllegalBlockSizeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rnt.SecuritySystem.entity.TempUserBlock;
import com.rnt.SecuritySystem.repo.TempUserBlockRepo;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

	
	@Autowired
	TempUserBlockRepo tempUserBlockRepo;
	
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {

		Instant start = Instant.now();
		return ResponseEntity.ok(ErrorResponse.builder()
				.message("Your session has expired. Please log in again to obtain a new access token.")
				.statusCode(HttpStatus.UNAUTHORIZED).trackStrace(ex.toString()).error(ex.getMessage())
				.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		Instant start = Instant.now();
		return ResponseEntity.ok(ErrorResponse.builder().message("Missing Path Variable")
				.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
				.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());

	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Instant start = Instant.now();
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		List<String> errorMessages = fieldErrors.stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.toList());

		return ResponseEntity.ok(ErrorResponse.builder().message("Validation failed").listOfMessage(errorMessages)
				.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
				.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());

	}

	@ExceptionHandler(InvalidKeyException.class)
	public ResponseEntity<ErrorResponse> handleInvalidKeyException(InvalidKeyException ex) {
		Instant start = Instant.now();
		return ResponseEntity.ok(
				ErrorResponse.builder().message("The AES encryption key is invalid. Please check").listOfMessage(null)
						.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
						.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());

	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Instant start = Instant.now();
		return ResponseEntity.ok(ErrorResponse.builder().message("Method Not Allowed").listOfMessage(null)
				.statusCode(HttpStatus.METHOD_NOT_ALLOWED).trackStrace(ex.toString()).error(ex.getMessage())
				.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());

	}

	@ExceptionHandler(CustomeException.class)
	public ResponseEntity<ErrorResponse> handleCustomeException(CustomeException ex) {
		Instant start = Instant.now();

		if (ex.getCause() instanceof BadCredentialsException || ex.getCause() instanceof InternalAuthenticationServiceException) {
			TempUserBlock findByIpAddress = tempUserBlockRepo.findByIpAddress(ex.getHttpServletRequest().getRemoteAddr());
			if(findByIpAddress==null) {
				TempUserBlock block=new TempUserBlock();
				block.setAttempt(1);
				block.setBlocked(false);
				block.setBlockedAt(LocalDateTime.now());
				block.setIpAddress(ex.getHttpServletRequest().getRemoteAddr());
				block.setReason("Try to Login");
				tempUserBlockRepo.save(block);
			}else {
 				if(Duration.between(findByIpAddress.getBlockedAt(), LocalDateTime.now()).toMillis()<=5000) {
					findByIpAddress.setAttempt(findByIpAddress.getAttempt()+1);
					findByIpAddress.setBlockedAt(LocalDateTime.now());
					tempUserBlockRepo.save(findByIpAddress);
				}else {
					findByIpAddress.setAttempt(1);
					findByIpAddress.setBlockedAt(LocalDateTime.now());
					tempUserBlockRepo.save(findByIpAddress);
				}
			}
			return ResponseEntity.ok(ErrorResponse.builder().message("Invalid username or password").listOfMessage(null)
					.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
					.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());
		}
		
		return ResponseEntity.ok(ErrorResponse.builder().message("An error occurred during authentication").listOfMessage(null)
				.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
				.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());

	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		Instant start = Instant.now();

		ErrorResponse error = ErrorResponse.builder()
				.message("The path you are trying to access does not exist: " + ex.getRequestURL())
				.success(Boolean.FALSE).statusCode(HttpStatus.NOT_FOUND).error("Path Not Found")
				.durationMs(Duration.between(start, Instant.now()).toMillis())
				.trackStrace(Arrays.toString(ex.getStackTrace())).build();

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(IllegalBlockSizeException.class)
	public ResponseEntity<ErrorResponse> handleIllegalBlockSizeException(IllegalBlockSizeException ex,
			HttpServletRequest request) {
		Instant start = Instant.now();

		return ResponseEntity
				.ok(ErrorResponse.builder().message("Encryption error occurred: " + ex.getMessage()).listOfMessage(null)
						.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
						.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());
	}
	@ExceptionHandler(ServletException.class)
	public ResponseEntity<ErrorResponse> handleIIllegalArgumentException(IllegalBlockSizeException ex,
			HttpServletRequest request) {
		Instant start = Instant.now();

		return ResponseEntity
				.ok(ErrorResponse.builder().message("Encryption error occurred: " + ex.getMessage()).listOfMessage(null)
						.statusCode(HttpStatus.BAD_REQUEST).trackStrace(ex.toString()).error(ex.getMessage())
						.success(Boolean.FALSE).durationMs(Duration.between(start, Instant.now()).toMillis()).build());
	}
	
	 
	 @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
			Instant start = Instant.now();
	
		 return ResponseEntity.ok(ErrorResponse.builder()
					.message("Token is malformed or not properly encrypted")
					.success(Boolean.FALSE).statusCode(HttpStatus.BAD_REQUEST).error(ex.getMessage())
					.durationMs(Duration.between(start, Instant.now()).toMillis())
					.trackStrace(Arrays.toString(ex.getStackTrace())).build());
	    }
	 
	 @ExceptionHandler(AccessDeniedException.class)
	    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
			Instant start = Instant.now();
	
		 return ResponseEntity.ok(ErrorResponse.builder()
					.message(ex.getMessage())
					.success(Boolean.FALSE).statusCode(HttpStatus.UNAUTHORIZED).error(ex.getMessage())
					.durationMs(Duration.between(start, Instant.now()).toMillis())
					.trackStrace(Arrays.toString(ex.getStackTrace())).build());
	    }
 
}
