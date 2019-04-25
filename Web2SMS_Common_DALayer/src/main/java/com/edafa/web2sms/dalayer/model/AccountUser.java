/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.AccountUserConst;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author khalid
 */
@Entity
@Table(name = "ACCOUNT_USERS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "AccountUser.findAll", query = "SELECT a FROM AccountUser a"),
		@NamedQuery(name = "AccountUser.findByAccountId", query = "SELECT a FROM AccountUser a WHERE a.account.accountId = :accountId AND a.status IN :statuses"),
		@NamedQuery(name = "AccountUser.countByAccountId", query = "SELECT COUNT(a) FROM AccountUser a WHERE a.account.accountId = :accountId AND a.status IN :statuses"),
		@NamedQuery(name = "AccountUser.findByAccountIdAndUsername", query = "SELECT a FROM AccountUser a WHERE a.account.accountId = :accountId AND a.username = :username AND a.status IN :statuses"),
		@NamedQuery(name = "AccountUser.findAdminByAccountId", query = "SELECT a FROM AccountUser a WHERE a.account.accountId = :accountId AND a.adminRoleFlag = true AND a.status IN :statuses"),
		@NamedQuery(name = "AccountUser.findByUsername", query = "SELECT a FROM AccountUser a WHERE a.username = :username"),
		@NamedQuery(name = "AccountUser.findByStatusId", query = "SELECT a FROM AccountUser a WHERE a.status = :statusId"),
		@NamedQuery(name = "AccountUser.findByAdminRoleFlag", query = "SELECT a FROM AccountUser a WHERE a.adminRoleFlag = :adminRoleFlag AND a.status IN :statuses"),
		@NamedQuery(name = "AccountUser.findByEmail", query = "SELECT a FROM AccountUser a WHERE a.email = :email"),
		@NamedQuery(name = "AccountUser.findByPhoneNumber", query = "SELECT a FROM AccountUser a WHERE a.phoneNumber = :phoneNumber"),
		@NamedQuery(name = "AccountUser.findByName", query = "SELECT a FROM AccountUser a WHERE a.name = :name"), })
public class AccountUser implements Serializable, AccountUserConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@SequenceGenerator(name = "AccountUserIdSeq", sequenceName = "ACCT_USER_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountUserIdSeq")
	@Size(min = 1, max = 20)
	@Column(name = "ACCOUNT_USER_ID")
	private String accountUserId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "USERNAME")
	private String username;
	@NotNull
	@Column(name = "ACCOUNT_ID")
	private String accountId;
	@JoinColumn(name = "STATUS_ID", referencedColumnName = "ACCOUNT_STATUS_ID")
	@ManyToOne
	private AccountStatus status;
	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Account account;
	@Column(name = "ADMIN_ROLE_FLAG")
	private Boolean adminRoleFlag;
	@ManyToMany()
	@JoinTable(name = "ACCOUNT_GROUP_USERS", joinColumns = {
			@JoinColumn(name = "ACCOUNT_USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ACCOUNT_GROUP_ID") })
	private List<AccountGroup> accountGroups;

	//@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid email") //this annotation to enforce email address validation
	@Size(max = 320)
	@Column(name = "EMAIL")
	private String email;
	@Size(max = 20)
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	@Size(max = 50)
	@Column(name = "NAME")
	private String name;
        
        @OneToOne(mappedBy = "accountUserId", targetEntity = AccountUserLogin.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        AccountUserLogin accountUserLogin;

	public AccountUser() {
	}

	public AccountUser(Account account, String username) {
		this.account = account;
		this.username = username;
	}

	public AccountUser(String id) {
		this.accountUserId = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public String getAccountUserId() {
		return accountUserId;
	}

	public void setAccountUserId(String accountUserId) {
		this.accountUserId = accountUserId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;

	}

	public Boolean getAdminRoleFlag() {
		return adminRoleFlag;
	}

	public void setAdminRoleFlag(Boolean adminRoleFlag) {
		this.adminRoleFlag = adminRoleFlag;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAccountGroups(List<AccountGroup> accountGroups) {
		this.accountGroups = accountGroups;
	}

	public List<AccountGroup> getAccountGroups() {
		return accountGroups;
	}

	public List<AccountGroup> getVisibleAccountGroups() {
		List<AccountGroup> vaccountGroups = new ArrayList<AccountGroup>();

		for (Iterator<?> iterator = getAccountGroups().iterator(); iterator.hasNext();) {
			AccountGroup accountGroup = (AccountGroup) iterator.next();
			if (!accountGroup.getHiddenFlag()) {
				vaccountGroups.add(accountGroup);
			}
		}

		return vaccountGroups;
	}

    public AccountUserLogin getAccountUserLogin() {
        return accountUserLogin;
    }

    public void setAccountUserLogin(AccountUserLogin accountUserLogin) {
        this.accountUserLogin = accountUserLogin;
    }
        
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountUserId != null ? accountUserId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof AccountUser)) {
			return false;
		}
		AccountUser other = (AccountUser) object;

		if ((this.accountUserId == null && other.accountUserId != null)
				|| (this.accountUserId != null && !this.accountUserId.equals(other.accountUserId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AccountUser [accountUserId=" + accountUserId + ", username=" + username + ", accountId=" + accountId
                                + ", name=" + name + ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", status=" + status + ", account=" + account + ", adminRoleFlag=" + adminRoleFlag + ", userGroups="
				+ accountGroups + "]";
	}

}
