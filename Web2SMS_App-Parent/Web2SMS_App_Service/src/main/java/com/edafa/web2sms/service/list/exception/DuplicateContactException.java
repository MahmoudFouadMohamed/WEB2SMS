package com.edafa.web2sms.service.list.exception;

import java.util.List;

import com.edafa.web2sms.dalayer.model.Contact;

/**
 * @author khalid
 *
 */
public class DuplicateContactException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4549037645428454346L;
	
	Contact contact;
	List<Contact> contacts;

	/*
	 *  True if entire ContactList is duplicated
	 *  False if some of contacts are duplicated and other are new
	 */
	boolean entireListFlag;
	
	
	public DuplicateContactException(Contact contact)
	{
		this.contact = contact;
	}

	public DuplicateContactException(List<Contact> contacts, boolean capacityFlag) {
		this.contacts = contacts;
		this.entireListFlag = capacityFlag;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public boolean isEntireListFlag() {
		return entireListFlag;
	}

	public void setEntireListFlag(boolean capacityFlag) {
		this.entireListFlag = capacityFlag;
	}
	
	

}
