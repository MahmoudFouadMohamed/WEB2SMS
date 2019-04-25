package com.edafa.web2sms.acc_manag.service.account.group.exception;

public class NotRemovableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -510373516356452895L;

	public NotRemovableException() {

	}

	public NotRemovableException(String message) {
		super(message);
	}

	public NotRemovableException(Throwable cause) {
		super(cause);
	}

	public NotRemovableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotRemovableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
