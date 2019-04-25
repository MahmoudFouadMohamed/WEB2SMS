package com.edafa.web2sms.acc_manag.service.account.exception;

public class ProvRequestNotFoundException extends Exception {

	String requestId;

	private static final long serialVersionUID = 1552402179210404759L;

	public ProvRequestNotFoundException(String requestId) {
		super();
		this.requestId = requestId;
	}

	public ProvRequestNotFoundException(String requestId, String message, Throwable cause) {
		super(message, cause);
		this.requestId = requestId;
	}

	public ProvRequestNotFoundException(String requestId, String message) {
		super(message);
		this.requestId = requestId;
	}

	public ProvRequestNotFoundException(String requestId, Throwable cause) {
		super(cause);
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	@Override
	public String getMessage() {
		if (super.getMessage() != null)
			return super.getMessage();
		return "Provisioning request with id (" + requestId + ") not found ";
	}

}
