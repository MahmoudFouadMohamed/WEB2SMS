package com.edafa.web2sms.service.list.exception;


public class ListNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5349235408346622553L;
	String listName;
	
	public ListNotFoundException(String listName) {
		
		this.listName = listName;

	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
	

}
