package com.edafa.web2sms.acc_manag.service.account.exception;

public class SenderNameAlreadyAttached extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9109757922101869538L;

	protected String accountId;
	protected String senderName;

	public SenderNameAlreadyAttached(String senderName, String accountId) {
		super("This sender name \"" + senderName + "\" is attached to another account with id (" + accountId + ")");
		this.accountId = accountId;
		this.senderName = senderName;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getSenderName() {
		return senderName;
	}
}
