package com.edafa.web2sms.service.prov.exception;

import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;

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

	public InvalidProvRequestException(ProvRequestTypeName provRequestType, String message) {


		super(provRequestType.toString()+": "+message);
	}
}
