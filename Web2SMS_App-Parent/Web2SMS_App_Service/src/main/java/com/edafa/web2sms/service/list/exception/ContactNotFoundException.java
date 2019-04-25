package com.edafa.web2sms.service.list.exception;

import com.edafa.web2sms.dalayer.model.Contact;

public class ContactNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1545367521276579047L;
	Contact contact;
	
	public ContactNotFoundException(Contact contact) {
		
		this.contact = contact;
	}

}
