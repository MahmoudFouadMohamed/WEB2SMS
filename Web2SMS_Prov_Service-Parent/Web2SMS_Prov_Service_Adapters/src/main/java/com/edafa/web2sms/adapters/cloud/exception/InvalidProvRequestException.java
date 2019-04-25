package com.edafa.web2sms.adapters.cloud.exception;

public class InvalidProvRequestException extends Exception {


	private static final long serialVersionUID = -4744299855579209813L;

	public InvalidProvRequestException() {

	}

	public InvalidProvRequestException(String message) {
		super(message);

	}

	public InvalidProvRequestException(Throwable cause) {
		super(cause);

	}

	public InvalidProvRequestException(String message, Throwable cause) {
		super(message, cause);

	}
}
