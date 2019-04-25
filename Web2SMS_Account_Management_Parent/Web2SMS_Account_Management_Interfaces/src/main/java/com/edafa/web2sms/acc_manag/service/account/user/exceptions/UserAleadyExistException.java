package com.edafa.web2sms.acc_manag.service.account.user.exceptions;

public class UserAleadyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String accountId;
	public String user;

	public UserAleadyExistException() {
	}

	public UserAleadyExistException(String accountId) {
		this.accountId = accountId;
		this.user = null;
	}
	
	public UserAleadyExistException(String accountId, String user) {
		this.accountId = accountId;
		this.user = user;
	}

	@Override
	public String getMessage() {
		String message;
		if ((message = super.getMessage()) != null)
			return message;
		if (accountId != null)
			return "Account(" + accountId + ") has no user(s) found ";
		else if(user != null)
			return "Account(" + accountId + ") has no user("+user+")";
		return "Account not found ";
	}

}
