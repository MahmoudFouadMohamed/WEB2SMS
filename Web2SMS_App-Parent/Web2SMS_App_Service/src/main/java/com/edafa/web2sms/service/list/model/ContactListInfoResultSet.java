package com.edafa.web2sms.service.list.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(name = "contactListInfoResultSet", namespace = "http://www.edafa.com/web2sms/service/list/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactListInfoResultSet extends ResultStatus {

	@XmlElement(required = true, nillable = false)
	List<ContactListInfoModel> contactListInfoResultSet;

	public List<ContactListInfoModel> getContactListInfoResultSet() {
		return contactListInfoResultSet;
	}

	public void setContactListInfoResultSet(List<ContactListInfoModel> contactListInfoResultSet) {
		this.contactListInfoResultSet = contactListInfoResultSet;
	}

}
