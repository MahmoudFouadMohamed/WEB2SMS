package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.ListTypeName;

@XmlType(name = "ContactListInfo", namespace = "http://www.edafa.com/web2sms/service/model/")
public class ContactListInfoModel {
	@XmlElement(required = true, nillable = true)
	private int listId;
	@XmlElement(required = true, nillable = false)
	private String listName;
	@XmlElement(required = true, nillable = true)
	private String description;
	@XmlElement(required = true, nillable = true)
	private Integer contactsCount;
	@XmlElement(required = true, nillable = true)
	private Integer totalSubmittedSMSCount;
	@XmlElement(required = true, nillable = true)
	private Integer submittedSMSCount;
	@XmlElement(required = true, nillable = true)
	private ListTypeName listType;

	public ContactListInfoModel() {
	}

	public ContactListInfoModel(int listId, String listName, String description) {
		this.listId = listId;
		this.listName = listName;
		this.description = description;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContactsCount(Integer contactsCount) {
		this.contactsCount = contactsCount;
	}

	public Integer getContactsCount() {
		return contactsCount;
	}

	public boolean isTempList() {
		return (listType == ListTypeName.TEMP_LIST);
	}

	public ListTypeName getListType() {
		return listType;
	}

	public void setListType(ListTypeName listType) {
		this.listType = listType;
	}

	public Integer getTotalSubmittedSMSCount() {
		return totalSubmittedSMSCount;
	}

	public void setTotalSubmittedSMSCount(Integer totalSubmittedSMSCount) {
		this.totalSubmittedSMSCount = totalSubmittedSMSCount;
	}

	public Integer getSubmittedSMSCount() {
		return submittedSMSCount;
	}

	public void setSubmittedSMSCount(Integer submittedSMSCount) {
		this.submittedSMSCount = submittedSMSCount;
	}

	public boolean isValid() {
		if (this.listName != null && !this.listName.equals("")) {
			return true;
		}
		return false;
	}

	public String toString() {
		String str = "List Name: " + listName + ", List ID: " + listId + ", List Description: " + description
				+ " and list contain: ";
		if (contactsCount != null) {
			str += contactsCount;
			str += contactsCount > 1 ? "Contacts" : "Contact";
		}
		return str;
	}

}
