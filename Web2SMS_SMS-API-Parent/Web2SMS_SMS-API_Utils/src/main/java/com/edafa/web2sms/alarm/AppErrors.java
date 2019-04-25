package com.edafa.web2sms.alarm;

public enum AppErrors {
	GENERAL_ERROR, EXCEED_REQUEST_MAX_SMS, FAILED_TO_FORWARD_REQUEST, INVALID_REQUEST;
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
