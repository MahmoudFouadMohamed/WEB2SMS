/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.ContactConst;

/**
 * 
 * @author yyaseen
 */
/**
 * @author akhalifah
 * 
 */
@Entity
@Table(name = "LIST_CONTACTS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Contact.findAll", query = "SELECT l FROM Contact l"),
		@NamedQuery(name = "Contact.findByListId", query = "SELECT l FROM Contact l WHERE l.listContactPK.listId = :listId "),
		@NamedQuery(name = "Contact.findByFirstName", query = "SELECT l FROM Contact l WHERE l.firstName = :firstName"),
		@NamedQuery(name = "Contact.findByLastName", query = "SELECT l FROM Contact l WHERE l.lastName = :lastName"),
		@NamedQuery(name = "Contact.findContactByCampIdAndStatus", query = "SELECT l FROM SMSLog s, Contact l  WHERE s.serviceInfo.listId = l.listContactPK.listId AND SUBSTRING(s.receiver,(length(s.receiver)-9), length(s.receiver)) = SUBSTRING(l.listContactPK.msisdn,(length(l.listContactPK.msisdn)-9),length(l.listContactPK.msisdn)) AND s.campaign.campaignId = :campaignId AND s.status IN :statusList "),
		@NamedQuery(name = "Contact.updateContact", query = "UPDATE Contact c SET c.listContactPK.msisdn= :msisdn , c.firstName= :firstName, c.lastName= :lastName ,c.value1= :value1,c.value2= :value2,c.value3= :value3,c.value4= :value4,c.value5= :value5 WHERE c.listContactPK.msisdn = :oldMsisdn AND c.listContactPK.listId =:listId"),
		@NamedQuery(name = "Contact.searchList", query = "SELECT l FROM Contact l WHERE l.listContactPK.listId = :listId AND (l.listContactPK.msisdn LIKE :msisdn OR l.firstName LIKE :firstName)"),
		@NamedQuery(name = "Contact.countByMsisdnAndListId", query = "SELECT COUNT(l) FROM Contact l WHERE l.listContactPK.msisdn = :msisdn AND l.listContactPK.listId = :listId"),
		@NamedQuery(name = "Contact.countByListId", query = "SELECT COUNT(l) FROM Contact l WHERE l.listContactPK.listId = :listId"),
		@NamedQuery(name = "Contact.removeByListId", query = "DELETE FROM Contact l WHERE l.listContactPK.listId = :listId"),
//		@NamedQuery(name = "Contact.compareListsByListIds", query = "SELECT l from Contact l where l.listContactPK.listId=:newSubListId and l.listContactPK.msisdn not in (Select s from Contact s where s.listContactPK.listId=:InternalListId)"),

		@NamedQuery(name = "Contact.findByMsisdn", query = "SELECT l FROM Contact l WHERE l.listContactPK.msisdn = :msisdn") })
@NamedNativeQueries({ @NamedNativeQuery(name = "Contact.copyList", query = "INSERT INTO LIST_CONTACTS (LIST_ID, FIRST_NAME, LAST_NAME, MSISDN, VALUE_1, VALUE_2, VALUE_3, VALUE_4, VALUE_5) "
		+ "SELECT ?, FIRST_NAME, LAST_NAME, MSISDN, VALUE_1, VALUE_2, VALUE_3, VALUE_4, VALUE_5 FROM LIST_CONTACTS  WHERE LIST_ID = ?") ,
@NamedNativeQuery(name="Contact.compareListsByListIds", query="SELECT MSISDN from LIST_CONTACTS  where LIST_ID= ? and MSISDN not in (Select MSISDN from LIST_CONTACTS  where LIST_ID=?)"),
@NamedNativeQuery(name="Contact.DeleteContactsFromList", query="Delete  from LIST_CONTACTS where LIST_ID = ? and MSISDN in ?"),
@NamedNativeQuery(name="Contact.ComapreAndDeleteDifference", query="DELETE from LIST_CONTACTS where LIST_ID = ? and MSISDN in(SELECT MSISDN from LIST_CONTACTS  where LIST_ID= ? and MSISDN not in (Select MSISDN from LIST_CONTACTS  where LIST_ID=?))")})
public class Contact implements Serializable, ContactConst {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	protected ListContactPK listContactPK;
	@Size(max = 50)
	@Column(name = "FIRST_NAME")
	private String firstName;
	@Size(max = 50)
	@Column(name = "LAST_NAME")
	private String lastName;
	@Size(max = 50)
	@Column(name = "VALUE_1")
	private String value1;
	@Size(max = 50)
	@Column(name = "VALUE_2")
	private String value2;
	@Size(max = 50)
	@Column(name = "VALUE_3")
	private String value3;
	@Size(max = 50)
	@Column(name = "VALUE_4")
	private String value4;
	@Size(max = 50)
	@Column(name = "VALUE_5")
	private String value5;
	@JoinColumn(name = "LIST_ID", referencedColumnName = "LIST_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private ContactList contactList;

	public Contact() {
	}

	public Contact(ListContactPK listContactPK) {
		this.listContactPK = listContactPK;
	}

	public Contact(Integer listId, String msisdn) {
		this.listContactPK = new ListContactPK(listId, msisdn);
	}

	public ListContactPK getListContactsPK() {
		return listContactPK;
	}

	public void setListContactsPK(ListContactPK listContactsPK) {
		this.listContactPK = listContactsPK;
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

	public ContactList getAccountList() {
		return contactList;
	}

	public void setAccountList(ContactList contactList) {
		this.contactList = contactList;
	}

	// public ContactListView getContactListView() {
	// return contactListView;
	// }
	//
	// public void setContactListView(ContactListView contactListView) {
	// this.contactListView = contactListView;
	// }

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (listContactPK != null ? listContactPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Contact)) {
			return false;
		}
		Contact other = (Contact) object;
		if ((this.listContactPK != null && other.listContactPK != null && this.listContactPK
				.equals(other.listContactPK))) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Contact (firstName=" + firstName + ", lastName=" + lastName + ", MSISDN=" + listContactPK.getMsisdn()
				+ (value1 != null ? ", value1= " + value1 : "") + (value2 != null ? ", value2= " + value2 : "")
				+ (value3 != null ? ", value3= " + value3 : "") + (value4 != null ? ", value4= " + value4 : "")
				+ (value5 != null ? ", value5= " + value5 : "") + ")";
	}
}
