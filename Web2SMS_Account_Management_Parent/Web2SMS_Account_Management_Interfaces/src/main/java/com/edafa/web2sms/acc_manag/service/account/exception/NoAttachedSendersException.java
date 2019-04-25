package com.edafa.web2sms.acc_manag.service.account.exception;

public class NoAttachedSendersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3868340249156740645L;
	String accountId;

	public NoAttachedSendersException() {
	}

	public NoAttachedSendersException(String accountId) {
		this.accountId = accountId;
	}

	public NoAttachedSendersException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return "No attached senders to account(" + accountId + ")";
	}
}
