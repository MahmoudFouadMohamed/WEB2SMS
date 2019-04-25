package com.edafa.web2sms.acc_manag.utils.sms.exception;

public class AccountManagInvalidSMSSender extends Exception {

	String sender;

	private static final long serialVersionUID = -2080577803412983883L;

	public AccountManagInvalidSMSSender(String sender) {
		super("Invalid sender: " + sender);
		this.sender = sender;
	}

	public AccountManagInvalidSMSSender(String sender, String message) {
		super(message);
		this.sender = sender;
	}

	@Override
	public String toString() {
		return getMessage();
	}

	public String getSender() {
		return sender;
	}

}
