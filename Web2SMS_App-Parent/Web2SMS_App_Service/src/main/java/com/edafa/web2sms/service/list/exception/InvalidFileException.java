package com.edafa.web2sms.service.list.exception;

public class InvalidFileException extends Exception {

	protected String message;

	public InvalidFileException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3470725005221906582L;

}
