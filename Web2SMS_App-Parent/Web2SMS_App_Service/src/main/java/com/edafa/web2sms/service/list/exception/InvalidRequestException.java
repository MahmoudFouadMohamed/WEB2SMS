package com.edafa.web2sms.service.list.exception;

public class InvalidRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2226873340080283624L;
	
	protected String message;

	public InvalidRequestException(String message) {
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
