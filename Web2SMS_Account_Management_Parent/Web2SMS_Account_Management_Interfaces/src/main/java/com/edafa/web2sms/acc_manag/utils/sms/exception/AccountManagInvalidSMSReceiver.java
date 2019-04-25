package com.edafa.web2sms.acc_manag.utils.sms.exception;

public class AccountManagInvalidSMSReceiver extends Exception {

	String receiver;
	
	private static final long serialVersionUID = -2080577803412983883L;

	public AccountManagInvalidSMSReceiver(String receiver) {
		super();
		this.receiver = receiver;
	}

	public AccountManagInvalidSMSReceiver(String receiver,String message) {
		super(message);
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		return getMessage();
	}
	
	public String getReceiver() {
		return receiver;
	}
}
