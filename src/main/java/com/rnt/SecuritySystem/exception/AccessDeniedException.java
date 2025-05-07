package com.rnt.SecuritySystem.exception;

public class AccessDeniedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2844344867340855988L;

	public AccessDeniedException(String message) {
		super(message);
	}

}
