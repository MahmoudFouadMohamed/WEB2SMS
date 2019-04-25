package com.edafa.web2sms.dalayer.exception;

public class QuotaInquiryFailed extends Exception {

	private static final long serialVersionUID = 4029335376008147109L;
	protected int errCode;

	public QuotaInquiryFailed() {
		// TODO Auto-generated constructor stub
	}

	public QuotaInquiryFailed(int errCode, String message) {
		super("ErrCode:" + errCode + ", Message: \"" + message + "\"");
		this.errCode = errCode;
	}

	public QuotaInquiryFailed(String message) {
		super(message);
	}

	public int getErrCode() {
		return errCode;
	}
}
