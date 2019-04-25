package com.edafa.web2sms.acc_manag.service.account.exception;

import com.edafa.web2sms.dalayer.model.AccountSender;

public class InvalidSenderType extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2460516628649003855L;
	protected AccountSender senderName;

	public InvalidSenderType(AccountSender senderName) {
		super("This sender name \""
				+ senderName.getAccountSendersPK().getSenderName()
				+ "\" has invalid sender type with id ("
				+ senderName.getAccountSendersPK().getAccountId() + ")");
		this.senderName = senderName;
	}

	public AccountSender getSenderName() {
		return senderName;
	}

	public void setSenderName(AccountSender senderName) {
		this.senderName = senderName;
	}

}
