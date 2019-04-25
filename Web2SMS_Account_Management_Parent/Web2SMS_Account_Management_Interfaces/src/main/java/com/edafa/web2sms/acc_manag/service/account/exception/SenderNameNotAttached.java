package com.edafa.web2sms.acc_manag.service.account.exception;

public class SenderNameNotAttached extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6225704766477860249L;
	
	protected String senderName;

	public SenderNameNotAttached(String senderName) {
		super("This sender name \"" + senderName + "\" is not attached to any account )");
		this.senderName = senderName;
	}

	public SenderNameNotAttached() {
		super("Sender name is not defined for this account");
		this.senderName = null;
	}

	public String getSenderName() {
		return senderName;
	}

}
