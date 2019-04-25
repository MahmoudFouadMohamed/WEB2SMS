package com.edafa.web2sms.service.report.exception;

public class CampaginReportNotFound extends Exception {

	private static final long serialVersionUID = 4029335376008147109L;

	public CampaginReportNotFound() {}

	public CampaginReportNotFound(String campId) {
		super("No Report found for [" + campId + "].");
	}
}
