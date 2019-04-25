package com.edafa.web2sms.service.api.sms.exceptions;

public class AccountSMSApiNotFoundException extends Exception {

//	private static final long serialVersionUID = -6501998843313975215L;

	public AccountSMSApiNotFoundException() {
		super();
	}

	public AccountSMSApiNotFoundException(String accountId) {
		super((accountId == null ? "account not found, the field accountId should not be null"
						: "account with id(" + accountId + ") less than min password length"));
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		// TODO Auto-generated method stub
		return this;
	}
}
