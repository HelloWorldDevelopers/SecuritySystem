package com.rnt.SecuritySystem.exception;

 
public class MaliciousPayloadException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8267723178841239793L;

	public MaliciousPayloadException(String message) {
        super(message);
    }
}
