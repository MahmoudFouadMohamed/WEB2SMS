/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.AccountGroupConst;

/**
 *
 * @author memad
 */
@Entity
@Table(name = "ACCOUNT_GROUPS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "AccountGroup.findAll", query = "SELECT a FROM AccountGroup a"),
		@NamedQuery(name = "AccountGroup.findByAccountGroupId", query = "SELECT a FROM AccountGroup a WHERE a.accountGroupId = :accountGroupId"),
		@NamedQuery(name = "AccountGroup.countByAccountId", query = "SELECT COUNT(a) FROM AccountGroup a WHERE a.account.accountId = :accountId AND a.hiddenFlag = FALSE"),
		@NamedQuery(name = "AccountGroup.findByAccountId", query = "SELECT a FROM AccountGroup a WHERE a.account.accountId = :accountId AND a.hiddenFlag = FALSE"),
		@NamedQuery(name = "AccountGroup.findByAccountIdAndGroupName", query = "SELECT a FROM AccountGroup a WHERE a.account.accountId = :accountId AND a.groupName = :groupName"),
		@NamedQuery(name = "AccountGroup.findByAccountIdAndDefaultFlag", query = "SELECT a FROM AccountGroup a WHERE a.account.accountId = :accountId AND a.defaultFlag = :defaultFlag"),
		@NamedQuery(name = "AccountGroup.findByHiddenFlag", query = "SELECT a FROM AccountGroup a WHERE a.hiddenFlag = :hiddenFlag"),
		@NamedQuery(name = "AccountGroup.findByCreationDate", query = "SELECT a FROM AccountGroup a WHERE a.creationDate = :creationDate") })
public class AccountGroup implements Serializable, AccountGroupConst {

	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@SequenceGenerator(name = "AccountGroupIdSeq", sequenceName = "ACCT_GROUP_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountGroupIdSeq")
	@Size(min = 1, max = 20)
	@Column(name = "ACCOUNT_GROUP_ID")
	private String accountGroupId;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "GROUP_NAME")
	private String groupName;
	@Column(name = "HIDDEN_FLAG")
	private Boolean hiddenFlag;
	@Column(name = "DEFAULT_FLAG")
	private Boolean defaultFlag;
	@Basic(optional = false)
	@NotNull
	@Column(name = "CREATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Account account;

	@JoinTable(name = "ACCOUNT_GROUP_USERS", joinColumns = {
			@JoinColumn(name = "ACCOUNT_GROUP_ID", referencedColumnName = "ACCOUNT_GROUP_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "ACCOUNT_USER_ID", referencedColumnName = "ACCOUNT_USER_ID") })
	@ManyToMany
	private List<AccountUser> accountUsers;

	@JoinTable(name = "ACCOUNT_GROUP_SYS_PRIVILEGES", joinColumns = {
			@JoinColumn(name = "ACCOUNT_GROUP_ID", referencedColumnName = "ACCOUNT_GROUP_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "PRIVILEGE_ID") })
	@ManyToMany
	private List<Privilege> privileges;

	public AccountGroup() {
	}

	public AccountGroup(String accountGroupId) {
		this.accountGroupId = accountGroupId;
	}

	public AccountGroup(String accountGroupId, Date creationDate) {
		this.accountGroupId = accountGroupId;
		this.creationDate = creationDate;
	}

	public String getAccountGroupId() {
		return accountGroupId;
	}

	public void setAccountGroupId(String accountGroupId) {
		this.accountGroupId = accountGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Boolean getHiddenFlag() {
		return hiddenFlag;
	}

	public void setHiddenFlag(Boolean hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}

	public Boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(Boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@XmlTransient
	public List<AccountUser> getAccountUsers() {
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUser> accountUsers) {
		this.accountUsers = accountUsers;
	}

	@XmlTransient
	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public void addPrivilege(Privilege privilege) {
		this.privileges.add(privilege);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountGroupId != null ? accountGroupId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof AccountGroup)) {
			return false;
		}
		AccountGroup other = (AccountGroup) object;
		if ((this.accountGroupId == null && other.accountGroupId != null)
				|| (this.accountGroupId != null && !this.accountGroupId.equals(other.accountGroupId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AccountGroup[ accountGroupId=" + accountGroupId + " ]";
	}

}
