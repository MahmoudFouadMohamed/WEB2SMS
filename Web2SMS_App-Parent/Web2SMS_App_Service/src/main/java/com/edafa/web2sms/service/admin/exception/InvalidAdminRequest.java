package com.edafa.web2sms.service.admin.exception;

public class InvalidAdminRequest extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7613050336324718168L;

	String message;

	public InvalidAdminRequest() {
		this.message = "please enter valid username/password ";
	}

	public InvalidAdminRequest(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
