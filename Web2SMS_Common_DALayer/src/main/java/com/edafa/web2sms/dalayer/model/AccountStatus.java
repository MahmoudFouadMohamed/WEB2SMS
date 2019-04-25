/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.model.constants.AccountStatusConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "ACCOUNT_STATUS")
@XmlRootElement
@ObjectTypeConverter(name = "AccountStatusNameConverter", dataType = java.lang.String.class, objectType = AccountStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "ACTIVE", objectValue = "ACTIVE"),
		@ConversionValue(dataValue = "SUSPENDED", objectValue = "SUSPENDED"),
		@ConversionValue(dataValue = "INACTIVE", objectValue = "INACTIVE") })
@NamedQueries({ @NamedQuery(name = "AccountStatus.findAll", query = "SELECT a FROM AccountStatus a"),
		@NamedQuery(name = "AccountStatus.findByAccountStatusId", query = "SELECT a FROM AccountStatus a WHERE a.accountStatus = :accountStatus"),
		@NamedQuery(name = "AccountStatus.findByAccountStatusName", query = "SELECT a FROM AccountStatus a WHERE a.accountStatusName = :accountStatusName"),
		@NamedQuery(name = "AccountStatus.findByDescription", query = "SELECT a FROM AccountStatus a WHERE a.description = :description") })
public class AccountStatus implements Serializable, AccountStatusConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ACCOUNT_STATUS_ID")
	private Integer accountStatus;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "ACCOUNT_STATUS_NAME")
	@Enumerated(EnumType.STRING)
	@Convert(value = "AccountStatusNameConverter")
	private AccountStatusName accountStatusName;
	@Size(max = 200)
	@Column(name = "DESCRIPTION")
	private String description;
	//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "status")
	@ManyToMany
	@JoinTable(name = "ACCOUNT_STATUS_USER_ACTIONS", joinColumns = {
			@JoinColumn(name = "ACCT_STATUS_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ACTION_ID") })
	private List<Action> action;
	private List<Account> accountList;

	@JoinTable(name = "ACCOUNT_STATUS_SYS_PRIVILEGES", joinColumns = {
			@JoinColumn(name = "ACCOUNT_STATUS_ID", referencedColumnName = "ACCOUNT_STATUS_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "PRIVILEGE_ID") })
	@ManyToMany
	private List<Privilege> privileges;

	public AccountStatus() {
	}

	public AccountStatus(Integer accountStatusId) {
		this.accountStatus = accountStatusId;
	}

	public AccountStatus(Integer accountStatusId, AccountStatusName accountStatusName) {
		this.accountStatus = accountStatusId;
		this.accountStatusName = accountStatusName;
	}

	public Integer getAccountStatusId() {
		return accountStatus;
	}

	public void setAccountStatusId(Integer accountStatusId) {
		this.accountStatus = accountStatusId;
	}

	public AccountStatusName getAccountStatusName() {
		return accountStatusName;
	}

	public void setAccountStatusName(AccountStatusName accountStatusName) {
		this.accountStatusName = accountStatusName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@XmlTransient
	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountStatus != null ? accountStatus.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof AccountStatus)) {
			return false;
		}
		AccountStatus other = (AccountStatus) object;
		if ((this.accountStatus == null && other.accountStatus != null)
				|| (this.accountStatus != null && !this.accountStatus.equals(other.accountStatus))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + accountStatus + "," + accountStatusName + ")";
	}

}
