package com.edafa.web2sms.service.admin.exception;

public class DuplicateUserNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6452105906370112084L;

	String name;
	
	
	public DuplicateUserNameException (String name)
	{
		this.name = name;
	}
	
	@Override
	public String getMessage()
	{
		
		return "Username: "+name+" already registered in database.";
	}
}
