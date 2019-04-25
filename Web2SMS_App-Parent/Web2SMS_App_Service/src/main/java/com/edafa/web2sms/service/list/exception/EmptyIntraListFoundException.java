package com.edafa.web2sms.service.list.exception;

public class EmptyIntraListFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5902004160715744963L;

	String accounttId;
	String message;
	public EmptyIntraListFoundException(String accountId){
		
		super( "Account("+accountId+") has no intra list in ODS.");
		this.accounttId = accountId;

	}
	
	
}
