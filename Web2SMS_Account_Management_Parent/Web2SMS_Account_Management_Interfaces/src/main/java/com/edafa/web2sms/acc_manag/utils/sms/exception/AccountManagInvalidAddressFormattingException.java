package com.edafa.web2sms.acc_manag.utils.sms.exception;

import com.edafa.web2sms.acc_manag.utils.sms.AccountManagMsisdnFormat;

public class AccountManagInvalidAddressFormattingException extends Exception {

	private static final long serialVersionUID = 1482535210256730472L;
	protected AccountManagMsisdnFormat sourceFormat;
	protected AccountManagMsisdnFormat destFormat;

	public AccountManagInvalidAddressFormattingException(AccountManagMsisdnFormat sourceFormat, AccountManagMsisdnFormat destFormat) {
		super("Cannot format address with " + sourceFormat.toString() + " format to " + destFormat.toString()
				+ " format");
		this.sourceFormat = sourceFormat;
		this.destFormat = destFormat;
	}

	public AccountManagMsisdnFormat getSourceFormat() {
		return sourceFormat;
	}

	public AccountManagMsisdnFormat getDestFormat() {
		return destFormat;
	}

}
