package com.edafa.web2sms.alarm;

public enum AppErrors {
	DATABASE_ERROR, DATABASE_TIMEOUT, GENERAL_ERROR, IO_ERROR, INELIGIBLE_ACCOUNT, AUTHORIZATION_FAILED, INVALID_REQUEST, INVALID_OPERATION,
        FAILED_TO_FORWARD_REQUEST;
	int id;

	private AppErrors() {
	}

	private AppErrors(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		System.out.println("AppErrors.setId()" + " " + name() + " id=" + id);
		this.id = id;
	}
}
