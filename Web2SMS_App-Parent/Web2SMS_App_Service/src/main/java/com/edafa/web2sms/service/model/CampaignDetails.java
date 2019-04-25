package com.edafa.web2sms.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignDetails", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class CampaignDetails {
	
	@XmlElement(required = true, nillable = false)
	List<ContactModel> individualContacts;

	@XmlElement(required = true, nillable = false)
	List<ContactListInfoModel> contactLists;

	public List<ContactListInfoModel> getContactLists() {
		return contactLists;
	}

	public void setContactLists(List<ContactListInfoModel> contactLists) {
		this.contactLists = contactLists;
	}

	public List<ContactModel> getIndividualContacts() {
		return individualContacts;
	}

	public void setIndividualContacts(List<ContactModel> individualContacts) {
		this.individualContacts = individualContacts;
	}
}
