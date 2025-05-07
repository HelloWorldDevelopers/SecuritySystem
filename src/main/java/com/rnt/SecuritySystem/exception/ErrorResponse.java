package com.rnt.SecuritySystem.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@JsonPropertyOrder({ "durationMs", "message", "success", "statusCode", "listOfMessage", "error", "trackStrace" })
public class ErrorResponse {

	private long durationMs;
	private String error;
	private String message;
	private HttpStatus statusCode;
	private String trackStrace;
	private List<String> listOfMessage;
	private Boolean success;
}
