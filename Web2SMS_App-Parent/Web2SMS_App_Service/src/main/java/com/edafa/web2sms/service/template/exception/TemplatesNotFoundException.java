package com.edafa.web2sms.service.template.exception;

import com.edafa.web2sms.service.model.UserTrxInfo;

public class TemplatesNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6822388418631903161L;
	
	private UserTrxInfo userTrxInfo;
	String errorMessage;

	
	public TemplatesNotFoundException() {
		this.errorMessage= "No Template found.";
	}
	
	
	public TemplatesNotFoundException(UserTrxInfo userTrxInfo) {
		this.userTrxInfo = userTrxInfo;
		this.errorMessage= userTrxInfo.logInfo()+"has no created Templates.";
	}
	
	public TemplatesNotFoundException(Integer templateId) {
		
		this.errorMessage= "Template with ID:"+templateId+" Not Found.";
		
		
	}
	
	@Override
	public String getMessage(){
		
		return this.errorMessage;
	}

}
