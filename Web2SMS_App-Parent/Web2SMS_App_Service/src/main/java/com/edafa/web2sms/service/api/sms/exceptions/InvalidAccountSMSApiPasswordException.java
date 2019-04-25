package com.edafa.web2sms.service.api.sms.exceptions;

public class InvalidAccountSMSApiPasswordException extends Exception
{
	private static final long serialVersionUID = 1L;
	private int allowedLength;
	
	public InvalidAccountSMSApiPasswordException()
	{
		super();
	}
	
	public InvalidAccountSMSApiPasswordException(String password,int allowedLength)
	{
		super("invalid_password_length");
		setAllowedLength(allowedLength);
	}

	public int getAllowedLength()
	{
		return allowedLength;
	}
	
	private void setAllowedLength(int allowedLength)
	{
		this.allowedLength = allowedLength;
	}
	
	@Override
	public synchronized Throwable fillInStackTrace() {
		// TODO Auto-generated method stub
		return this;
	}
}// end of class InvalidAccountSMSApiPasswordException
