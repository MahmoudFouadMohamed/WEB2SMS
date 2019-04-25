package com.edafa.web2sms.acc_manag.service.account.exception;

public class AccountNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8796088740631577662L;
	public String accountId;

	public AccountNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public AccountNotFoundException(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public String getMessage() {
		String message;
		if ((message = super.getMessage()) != null)
			return message;
		if (accountId != null)
			return "Account with id (" + accountId + ") is not found ";
		return "Account not found ";
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
	
	
}
