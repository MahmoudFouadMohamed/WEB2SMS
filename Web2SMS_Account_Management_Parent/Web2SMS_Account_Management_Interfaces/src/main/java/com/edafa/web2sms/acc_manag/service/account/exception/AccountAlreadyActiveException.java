package com.edafa.web2sms.acc_manag.service.account.exception;

public class AccountAlreadyActiveException extends Exception {

	String accountId;

	private static final long serialVersionUID = 7982195270848161306L;

	public AccountAlreadyActiveException(String accountId) {
		super();
		this.accountId = accountId;
	}

	public AccountAlreadyActiveException(String accountId, String message, Throwable cause) {
		super(message, cause);
		this.accountId = accountId;
	}

	public AccountAlreadyActiveException(String accountId, String message) {
		super(message);
		this.accountId = accountId;
	}

	public AccountAlreadyActiveException(String accountId, Throwable cause) {
		super(cause);
		this.accountId = accountId;
	}

	@Override
	public String getMessage() {
		return "The account is already active with id: " + accountId;
	}

}
