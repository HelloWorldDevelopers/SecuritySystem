package com.rnt.SecuritySystem.exception;

public class AccountBlockedException extends RuntimeException {
	public AccountBlockedException(String message) {
		super(message);
	}
}