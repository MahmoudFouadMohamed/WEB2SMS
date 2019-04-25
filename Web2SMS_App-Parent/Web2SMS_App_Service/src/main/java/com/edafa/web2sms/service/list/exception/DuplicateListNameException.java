package com.edafa.web2sms.service.list.exception;

public class DuplicateListNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6268125696294228562L;
	
	String listName;
	
	public DuplicateListNameException(String listName)
	{
		this.listName = listName;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
	

}
