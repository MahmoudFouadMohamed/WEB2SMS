package com.edafa.web2sms.service.campaign.exception;

import java.util.List;

public class InvalidCampListException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<Integer> lists;
	
	public InvalidCampListException(List<Integer> lists){
		super("Invalid or not ready lists"+lists);
		this.lists = lists;
	}

}
