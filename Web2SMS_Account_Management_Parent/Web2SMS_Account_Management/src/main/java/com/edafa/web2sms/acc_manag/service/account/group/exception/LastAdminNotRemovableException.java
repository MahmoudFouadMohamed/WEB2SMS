package com.edafa.web2sms.acc_manag.service.account.group.exception;

public class LastAdminNotRemovableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4925690153998620583L;

	public LastAdminNotRemovableException() {

	}

	public LastAdminNotRemovableException(String message) {
		super(message);
	}

	public LastAdminNotRemovableException(Throwable cause) {
		super(cause);
	}

	public LastAdminNotRemovableException(String message, Throwable cause) {
		super(message, cause);

	}

	public LastAdminNotRemovableException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
