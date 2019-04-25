package com.edafa.web2sms.service.report.exception;

public class ReportException extends Exception {

	private static final long serialVersionUID = 5650289521301962713L;

	public ReportException() {}

	public ReportException(String message) {
		super(message);
	}

	public ReportException(Throwable cause) {
		super(cause);
	}

	public ReportException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
