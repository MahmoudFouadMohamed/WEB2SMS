package com.edafa.web2sms.sms.exceptions;

public class SecretKeyDecryptionFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4857024224002630289L;

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
