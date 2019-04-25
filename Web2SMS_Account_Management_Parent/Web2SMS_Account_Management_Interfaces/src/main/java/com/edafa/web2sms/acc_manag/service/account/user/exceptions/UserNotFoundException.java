package com.edafa.web2sms.acc_manag.service.account.user.exceptions;

public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4307264187364167583L;
	
	public String accountId;
	public String user;

	public UserNotFoundException() {
		
	}

	public UserNotFoundException(String accountId) {
		this.accountId = accountId;
		this.user = null;
	}
	
	public UserNotFoundException(String accountId, String user) {
		this.accountId = accountId;
		this.user = user;
	}

	@Override
	public String getMessage() {
		
		if (user != null)
			return "Account(" + accountId + ") has no user("+user+")";
		else if(accountId != null)
			return "Account(" + accountId + ") has no user(s) found ";
		return "User not found ";
	}

}
