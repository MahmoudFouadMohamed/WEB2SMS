package com.edafa.web2sms.acc_manag.service.account.user.exceptions;

public class AdminAlreadyGrantedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accountId;
	private String user;
	private String message;

	public AdminAlreadyGrantedException() {
	}

	public AdminAlreadyGrantedException(String accountId) {
		this.accountId = accountId;
		this.user = null;
		this.message = null;
	}

	public AdminAlreadyGrantedException(String accountId, String user) {
		this.accountId = accountId;
		this.user = user;
		this.message = null;
	}

	public AdminAlreadyGrantedException(String accountId, String user, String message) {
		this.accountId = accountId;
		this.user = user;
		this.message = message;
	}

	@Override
	public String getMessage() {
		if (message != null && !message.isEmpty())
			return message;
		else if (user != null)
			message = "User(" + user + ") is the admin for account(" + accountId + ").";
		else if (accountId != null)
			message = "Account(" + accountId + ") already has admin user ";
		else
			message = "Admin already exist ";
		return message;
	}

}
