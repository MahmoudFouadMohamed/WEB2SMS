package com.edafa.web2sms.dalayer.enums;

public enum SMSStatusName {
    SUBMITTED, SENT, DELIVERED, TIMED_OUT, NOT_DELIVERED, FAILED_TO_SEND, FAILED, REJECTED, RECEIVED, UNKNOWN, EXPIRED;

	private String name;
	private int dbId;

	SMSStatusName() {
	}

	SMSStatusName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public int getDbId() {
		return dbId;
	}
}
