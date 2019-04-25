package com.edafa.web2sms.sms.exceptions;

public class AuthorizationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5727350703541639102L;

	protected String message;

	public AuthorizationFailedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
