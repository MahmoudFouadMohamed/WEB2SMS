package com.edafa.web2sms.service.report.exception;

import com.edafa.web2sms.service.model.HistoryReportSearch;

public class SMSAPIViewLogNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8897938305315193451L;

	String accountId;
	HistoryReportSearch params;

	public SMSAPIViewLogNotFoundException() {
		super();
	}

	public SMSAPIViewLogNotFoundException(String accountId, HistoryReportSearch params) {

		super(params == null ? "search params shouldn't be null"
				: ("There is no log for account (" + accountId + ") with " + params));
		this.accountId = accountId;
		this.params = params;
	}

	public SMSAPIViewLogNotFoundException(String accountId, HistoryReportSearch params, Throwable e) {
		super(params == null ? "search params shouldn't be null"
				: ("There is no log for account (" + accountId + ") with " + params));
		this.accountId = accountId;
		this.params = params;
	}

	public SMSAPIViewLogNotFoundException(Throwable cause) {
		super(cause);
	}

}
