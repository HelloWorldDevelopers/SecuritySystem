package com.rnt.SecuritySystem.exception;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomeException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	HttpServletRequest httpServletRequest;
	public CustomeException(String message, Throwable cause,HttpServletRequest httpServletRequest) {
		super(message, cause);
		this.httpServletRequest=httpServletRequest;
	}
}
