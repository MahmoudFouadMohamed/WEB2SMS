package com.edafa.web2sms.service.api.sms.exceptions;

import java.util.List;

public class InvalidIpsListException extends Exception {

	public InvalidIpsListException() {
		super("IPs List should not be null");
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		// TODO Auto-generated method stub
		return this;
	}
}
