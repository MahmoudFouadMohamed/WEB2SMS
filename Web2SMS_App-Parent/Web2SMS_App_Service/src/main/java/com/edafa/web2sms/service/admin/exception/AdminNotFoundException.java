package com.edafa.web2sms.service.admin.exception;

import java.math.BigDecimal;

public class AdminNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5154025281056308047L;

	
	public BigDecimal accountId;
	String userName;
	String errorMessage;

	public AdminNotFoundException(BigDecimal accountId) {
		this.accountId = accountId;
		errorMessage = "Admin with id (" + accountId + ") is not found ";
	}

	public AdminNotFoundException(String username) {
		this.userName = username;
		errorMessage = "Admin with user name (" + username + ") is not found ";
	}

	@Override
	public String getMessage() {
		return errorMessage;
		}
}
