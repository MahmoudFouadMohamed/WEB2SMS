package com.edafa.web2sms.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ContactList", namespace = "http://www.edafa.com/web2sms/service/model/")
public class ContactListModel {

	@XmlElement
	ContactListInfoModel listInfo;

	@XmlElement(required = true, nillable = false)
	private List<ContactModel> listContacts;

	public ContactListInfoModel getListInfo() {
		return listInfo;
	}

	public void setListInfo(ContactListInfoModel listInfo) {
		this.listInfo = listInfo;
	}

	public List<ContactModel> getListContacts() {
		return listContacts;
	}

	public void setListContacts(List<ContactModel> listContacts) {
		this.listContacts = listContacts;
	}
	
	
	@Override
	public String toString() {
		return "ContactListModel [listInfo=" + listInfo + ", listContacts=" + listContacts + "]";
	}

	public boolean isVaild(){
		
		if(listInfo.isValid()){
			if(!(listContacts.size()==0)){
				return true;
			}
		}
		return false;
	}

}
