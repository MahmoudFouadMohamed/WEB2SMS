package com.edafa.web2sms.service.list.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ResultStatus;

@XmlType(namespace = "http://www.edafa.com/web2sms/service/list/model/")
public class ContactListResultSet extends ResultStatus {

	@XmlElement(required = true, nillable = false)
	List<ContactListInfoModel> contactListInfoSet;

	public List<ContactListInfoModel> getContactListInfoSet() {
		return contactListInfoSet;
	}

	public void setContactListInfoSet(List<ContactListInfoModel> contactListInfoSet) {
		this.contactListInfoSet = contactListInfoSet;
	}

}
