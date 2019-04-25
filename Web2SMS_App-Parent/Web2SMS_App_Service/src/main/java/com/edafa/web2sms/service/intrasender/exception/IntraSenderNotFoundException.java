package com.edafa.web2sms.service.intrasender.exception;

import com.edafa.web2sms.service.model.UserTrxInfo;

public class IntraSenderNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6822388418631903161L;

	private UserTrxInfo userTrxInfo;
	String errorMessage;

	public IntraSenderNotFoundException() {
		this.errorMessage = "No intra sender found.";
	}

	public IntraSenderNotFoundException(UserTrxInfo userTrxInfo) {
		this.userTrxInfo = userTrxInfo;
		this.errorMessage = userTrxInfo.logInfo() + "has no created intra sender.";
	}

	public IntraSenderNotFoundException(String id) {

		this.errorMessage = "Intra sender with ID:" + id + " Not Found.";

	}

	@Override
	public String getMessage() {

		return this.errorMessage;
	}
}
