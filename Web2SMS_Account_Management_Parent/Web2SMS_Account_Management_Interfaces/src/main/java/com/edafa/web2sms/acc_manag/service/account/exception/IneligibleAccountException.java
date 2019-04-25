package com.edafa.web2sms.acc_manag.service.account.exception;

public class IneligibleAccountException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4834378246468094165L;
	
	
	public IneligibleAccountException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
