package com.edafa.web2sms.acc_manag.service.account.group.exception;

public class NotEditableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotEditableException() {

	}

	public NotEditableException(String message) {
		super(message);
	}

	public NotEditableException(Throwable cause) {
		super(cause);
	}

	public NotEditableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotEditableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
