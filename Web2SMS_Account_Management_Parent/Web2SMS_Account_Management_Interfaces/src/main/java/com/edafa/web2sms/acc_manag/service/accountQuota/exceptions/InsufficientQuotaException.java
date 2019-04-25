package com.edafa.web2sms.acc_manag.service.accountQuota.exceptions;

public class InsufficientQuotaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5092068665711512847L;

	
	public InsufficientQuotaException(String message){
		super(message);
	}
}
