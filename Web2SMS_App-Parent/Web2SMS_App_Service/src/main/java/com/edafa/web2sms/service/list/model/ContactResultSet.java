package com.edafa.web2sms.service.list.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(namespace = "http://www.edafa.com/web2sms/service/list/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactResultSet extends  ResultStatus{
	@XmlElement(required= true , nillable= false)
	List<ContactModel> contacts;


	public List<ContactModel> getContact() {
		return contacts;
	}

	public void setContact(List<ContactModel> contactResultSet) {
		this.contacts = contactResultSet;
	}

}
