package com.rnt.SecuritySystem.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
@JsonPropertyOrder({ "success", "message", "httpStatus", "durationMs", "accessToken", "refreshToken" })
public class TokenResponseBody {

	private String accessToken;
	private String refreshToken;
	private Boolean success;
	private String message;
	private HttpStatus httpStatus;
	private long durationMs;
 }
