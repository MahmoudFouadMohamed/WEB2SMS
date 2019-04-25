package com.edafa.web2sms.sms.exceptions;

public class AccountNotRegisteredOnAPIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4046495026814476816L;

	public String accountId;

	public AccountNotRegisteredOnAPIException() {
		// TODO Auto-generated constructor stub
	}

	public AccountNotRegisteredOnAPIException(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public String getMessage() {
		String message;
		if ((message = super.getMessage()) != null)
			return message;
		if (accountId != null)
			return "Account with id (" + accountId + ") is not registered on SMS-API ";
		return "Account is not registered on SMS-API";
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}



