/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.model.constants.ContactListConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "LISTS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ContactList.findAll", query = "SELECT l FROM ContactList l"),
		@NamedQuery(name = "ContactList.findByListId", query = "SELECT l FROM ContactList l WHERE l.listId = :listId"),
		@NamedQuery(name = "ContactList.findByListIds", query = "SELECT l FROM ContactList l WHERE l.listId IN :listIds"),
		@NamedQuery(name = "ContactList.findByListName", query = "SELECT l FROM ContactList l WHERE l.listName = :listName"),
		@NamedQuery(name = "ContactList.findByListIdAndAccountId", query = "SELECT l FROM ContactList l WHERE l.listId = :listId AND l.account.accountId =:accountId"),
		@NamedQuery(name = "ContactList.findByDescription", query = "SELECT l FROM ContactList l WHERE l.description = :description"),
		@NamedQuery(name = "ContactList.findByAccountId", query = "SELECT l FROM ContactList l WHERE l.account.accountId = :accountId"),
		@NamedQuery(name = "ContactList.findByAccountIdAndType", query = "SELECT l FROM ContactList l WHERE l.account.accountId = :accountId AND l.listType = :listType ORDER BY l.listName"),
		@NamedQuery(name = "ContactList.findByAccountIdAndTypes", query = "SELECT l FROM ContactList l WHERE l.account.accountId = :accountId AND l.listType IN :listTypes ORDER BY l.listType.listTypeId DESC, l.listName"),
		// @NamedQuery(name = "ContactList.findByAccountIdAndTypeWithCounts",
		// query =
		// "SELECT DISTINCT NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(c.listContactPK.listId)) FROM ContactList l LEFT JOIN Contact c ON l.listId = c.listContactPK.listId WHERE l.account.accountId = :accountId AND l.listType = :listType GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listName"),
		// @NamedQuery(name = "ContactList.findByAccountIdAndTypesWithCounts",
		// query =
		// " SELECT  NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(c.listContactPK.listId)) FROM  ContactList l LEFT JOIN Contact c ON l.listId = c.listContactPK.listId WHERE l.account.accountId = :accountId AND l.listType IN :listTypes GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listType.listTypeId DESC, l.listName "),
		@NamedQuery(name = "ContactList.findByListNameAndAccountId", query = " SELECT l FROM ContactList l WHERE l.listName = :listName AND l.account.accountId = :accountId"),
		@NamedQuery(name = "ContactList.getListIdByName", query = "SELECT l.listId FROM ContactList l WHERE l.listName = :listName"),
		@NamedQuery(name = "ContactList.countByListIdAndAccountId", query = "SELECT count(l) FROM ContactList l WHERE l.listId = :listId AND l.account.accountId =:accountId"),
		@NamedQuery(name = "ContactList.countByListNameAndAccountId", query = "SELECT count(l) FROM ContactList l WHERE l.listName = :listName AND l.account.accountId =:accountId"),
		@NamedQuery(name = "ContactList.countContactsInList", query = "SELECT COUNT(c.listContactPK.listId) FROM ContactList l, Contact c WHERE l.listId = c.listContactPK.listId AND l.listId = :listId"),
		@NamedQuery(name = "ContactList.countContactsInLists", query = "SELECT COUNT(c.listContactPK.listId) FROM ContactList l, Contact c WHERE l.listId = c.listContactPK.listId AND l.listId IN :listIds"),
		@NamedQuery(name = "ContactList.countAccountIdAndType", query = "SELECT COUNT(l.listId) FROM ContactList l WHERE l.account.accountId = :accountId AND l.listType = :listType"),
		@NamedQuery(name = "ContactList.countAccountIdAndTypes", query = "SELECT COUNT(l.listId) FROM ContactList l WHERE l.account.accountId = :accountId AND l.listType IN :listTypes"),
		@NamedQuery(name = "ContactList.updateListName", query = "UPDATE ContactList c SET c.listName = :listName WHERE c.listId = :listId AND c.account.accountId =:accountId"),
		@NamedQuery(name = "ContactList.searchByListNameAndType", query = "SELECT l FROM ContactList l WHERE l.account.accountId = :accountId AND LOWER(l.listName) LIKE :listName AND l.listType IN :listType"),

		// @NamedQuery(name = "ContactList.searchByListNameAndTypeWithCounts",
		// query =
		// "SELECT DISTINCT NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(c.listContactPK.listId)) FROM ContactList l LEFT JOIN Contact c ON l.listId = c.listContactPK.listId WHERE l.account.accountId = :accountId AND LOWER(l.listName) LIKE :listName AND l.listType = :listType GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listName"),
		@NamedQuery(name = "ContactList.removeByListId", query = "DELETE FROM ContactList l WHERE l.listId = :listId") })
@NamedNativeQueries({ 																	
	@NamedNativeQuery(name = "ContactList.findByListNameAndAccountIdNativeSql", query = "select LIST_ID from lists where LIST_NAME=? and ACCOUNT_ID= ?") })
public class ContactList implements Serializable, ContactListConst {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@NotNull
	@SequenceGenerator(name = "ListIdSeq", sequenceName = "LIST_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ListIdSeq")
	@Column(name = "LIST_ID")
	private Integer listId;
	@Basic(optional = false)
	// @Size(min = 1, max = 100)
	@Column(name = "LIST_NAME")
	private String listName;
	// @Size(max = 1000)
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CONTACT_COUNT")
	private long contactsCount;

	/*
	 * @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
	 * 
	 * @ManyToOne(optional = false, fetch = FetchType.LAZY) private Accounts
	 * accountId;
	 * 
	 * @JoinColumn(name = "LIST_TYPE_ID", referencedColumnName = "LIST_TYPE_ID")
	 * 
	 * @ManyToOne(optional = false, fetch = FetchType.LAZY) private ListType
	 * listTypeId;
	 */
	@JoinColumn(name = "LIST_TYPE_ID", referencedColumnName = "LIST_TYPE_ID")
	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.DETACH)
	private ListType listType;
	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
	@ManyToOne(optional = false)
	private Account account;
	@OneToMany(cascade = CascadeType.DETACH, mappedBy = "list")
	private List<CampaignLists> campaignListsList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "contactList")
	private List<Contact> contacts;

	public ContactList() {
	}

	public ContactList(Integer listId) {
		this.listId = listId;
	}

	public ContactList(Integer listId, String listName, ListType listType) {
		this.listId = listId;
		this.listName = listName;
		this.listType = listType;
	}

	public ContactList(Integer listId, String listName, String description, ListType listType, Long contactsCount) {
		this.listId = listId;
		this.listName = listName;
		this.listType = listType;
		this.description = description;
		this.contactsCount = contactsCount;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
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

	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setListContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Contact> getListContacts() {
		return contacts;
	}

	public Long getContactsCount() {
		return contactsCount;
	}

	public void setContactsCount(Long contactsCount) {
		this.contactsCount = contactsCount;
	}

	public boolean isListType(ListTypeName listTypeName) {
		return (listTypeName == listType.getListTypeName());
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (listId != null ? listId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ContactList)) {
			return false;
		}
		ContactList other = (ContactList) object;
		if ((this.listId == null && other.listId != null) || (this.listId != null && !this.listId.equals(other.listId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ContactList(listId=" + listId + ", listName=" + listName + ", description=" + description
				+ ", list type= " + listType + (contacts != null ? ", contacts count= " + contacts.size() : "") + ")";
	}

	public String logId() {
		return "ContactList(" + getListId() + ") ";
	}

}
