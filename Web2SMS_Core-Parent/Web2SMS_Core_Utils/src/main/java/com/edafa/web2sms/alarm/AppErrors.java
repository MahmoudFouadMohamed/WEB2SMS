package com.edafa.web2sms.alarm;

public enum AppErrors {
	DATABASE_ERROR, DATABASE_TIMEOUT, GENERAL_ERROR, IO_ERROR, CDR_TASK_ERROR, FATAL_ERROR, FAILED_OPERATION;
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
