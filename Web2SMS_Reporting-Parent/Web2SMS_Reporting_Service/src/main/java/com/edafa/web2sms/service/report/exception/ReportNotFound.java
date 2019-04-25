package com.edafa.web2sms.service.report.exception;

public class ReportNotFound extends ReportException {

	private static final long serialVersionUID = -5493624038879103590L;

	public ReportNotFound() {}

	public ReportNotFound(long reportId) {
		super("No Report found for [" + reportId + "].");
	}
}
