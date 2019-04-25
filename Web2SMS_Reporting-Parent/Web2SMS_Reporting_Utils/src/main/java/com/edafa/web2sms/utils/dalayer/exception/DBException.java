package com.edafa.web2sms.utils.dalayer.exception;

public final class DBException extends Exception {

	private static final long serialVersionUID = -3760351366981119113L;

	public DBException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DBException(Throwable e) {
		super(e);
	}

	public DBException(String str, Throwable e) {
		super(str, e);
	}

	public DBException(String message) {
		super(message);
	}

}
