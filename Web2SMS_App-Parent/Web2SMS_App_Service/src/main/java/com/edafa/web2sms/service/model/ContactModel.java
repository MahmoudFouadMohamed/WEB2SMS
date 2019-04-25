package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Contact", namespace = "http://www.edafa.com/web2sms/service/model/")
public class ContactModel {
	@XmlElement(required = true, nillable = true)
	String firstName;
	@XmlElement(required = true, nillable = true)
	String lastName;
	@XmlElement(required = true, nillable = false)
	String msisdn;
	@XmlElement(required = true, nillable = true)
	String value1;
	@XmlElement(required = true, nillable = true)
	String value2;
	@XmlElement(required = true, nillable = true)
	String value3;
	@XmlElement(required = true, nillable = true)
	String value4;
	@XmlElement(required = true, nillable = true)
	String value5;
	

	// @XmlElement(required = true, nillable = true)
	// Integer listId;

	public ContactModel() {

	}

	public ContactModel(String firstName, String lastName, String msisdn) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.msisdn = msisdn;
	}

	// public ContactModel(String firstName, String lastName, String msisdn,
	// Integer listId) {
	// super();
	// this.firstName = firstName;
	// this.lastName = lastName;
	// this.msisdn = msisdn;
	// this.listId = listId;
	// }

	public ContactModel(String msisdn) {
		super();
		this.msisdn = msisdn;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	// public Integer getListId() {
	// return listId;
	// }
	//
	// public void setListId(Integer listId) {
	// this.listId = listId;
	// }

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	public String getValue5() {
		return value5;
	}

	public void setValue5(String value5) {
		this.value5 = value5;
	}

	@Override
	public boolean equals(Object object) {

		if (!(object instanceof ContactModel)) {
			return false;
		}
		ContactModel cm = (ContactModel) object;

		if (this.msisdn.equals(cm.msisdn) && this.msisdn != null && !(this.msisdn.equals("")))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Contact (firstName=" + firstName + ", lastName=" + lastName + ", MSISDN=" + msisdn + ")";
	}
}
