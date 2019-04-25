package com.edafa.web2sms.sms.exceptions;

public class NotTrustedIPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6515488365639343614L;

	
	protected String message;

	public NotTrustedIPException(String message) {
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
