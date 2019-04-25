package com.edafa.web2sms.acc_manag.service.account.exception;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;

public class InvalidAccountStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9157207901273080522L;

	AccountStatusName currentStatus;
	ProvRequestTypeName requestedReqType;

	public InvalidAccountStateException(AccountStatusName status, ProvRequestTypeName requtestedProvAction) {
		this.currentStatus = status;
		this.requestedReqType = requtestedProvAction;
	}

	public AccountStatusName getStatus() {
		return currentStatus;
	}

	@Override
	public String getMessage() {
		return "Account with status= " + currentStatus + " is not valid for action " + requestedReqType;
	}
}
