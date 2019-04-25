package com.edafa.web2sms.quota.exceptions;

public class InSufficientQuotaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3438374266542575335L;
	
	public InSufficientQuotaException(String message){
		super(message);
	}

}
