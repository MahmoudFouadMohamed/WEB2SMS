package com.edafa.web2sms.dalayer.exception;

public class IntraListInquiryFailed extends Exception {
	private static final long serialVersionUID = 4029335376008147109L;
	protected int errCode;

	public IntraListInquiryFailed() {
		// TODO Auto-generated constructor stub
	}

	public IntraListInquiryFailed(int errCode, String message) {
		super("ErrCode:" + errCode + ", Message: \"" + message + "\"");
		this.errCode = errCode;
	}

	public IntraListInquiryFailed(String message) {
		super(message);
	}

	public int getErrCode() {
		return errCode;
	}
}
