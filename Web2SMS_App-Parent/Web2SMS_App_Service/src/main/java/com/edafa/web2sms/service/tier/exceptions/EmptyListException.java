package com.edafa.web2sms.service.tier.exceptions;

public class EmptyListException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7546298194613911514L;

	int first;
	
	public EmptyListException(int first)
	{
		this.first= first;
	}
	@Override
	public String getMessage() {

		return "ensure that first: "+first+" is  <= number of records.";
	}

}
