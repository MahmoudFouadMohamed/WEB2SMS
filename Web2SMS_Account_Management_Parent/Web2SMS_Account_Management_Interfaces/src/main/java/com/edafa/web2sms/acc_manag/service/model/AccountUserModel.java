package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import java.io.Serializable;

@XmlType(name = "AccountUser", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountUserModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(required = true, nillable = false)
	private String accountUserId;

	@XmlElement(required = true, nillable = false)
	private String username;

	@XmlElement(required = true, nillable = false)
	private AccountStatusName status;

	@XmlElement(required = true, nillable = false)
	private AccountModel account;

	@XmlElement(required = true, nillable = false)
	private Boolean adminRoleFlag;
	
	
	
	public AccountUserModel(){
		
	}
	
	public AccountUserModel(AccountUserModel accountUser) {
		this.account = accountUser.account;
		this.accountUserId = accountUser.accountUserId;
		this.adminRoleFlag = accountUser.adminRoleFlag;
		this.status = accountUser.status;
		this.username = accountUser.accountUserId;
	
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

	public AccountStatusName getStatus() {
		return status;
	}

	public void setStatus(AccountStatusName status) {
		this.status = status;
	}

	public AccountModel getAccount() {
		return account;
	}

	public void setAccount(AccountModel account) {
		this.account = account;
	}

	public Boolean getAdminRoleFlag() {
		return adminRoleFlag;
	}

	public void setAdminRoleFlag(Boolean adminRoleFlag) {
		this.adminRoleFlag = adminRoleFlag;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountUserModel other = (AccountUserModel) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (accountUserId == null) {
			if (other.accountUserId != null)
				return false;
		} else if (!accountUserId.equals(other.accountUserId))
			return false;
		if (adminRoleFlag == null) {
			if (other.adminRoleFlag != null)
				return false;
		} else if (!adminRoleFlag.equals(other.adminRoleFlag))
			return false;
		if (status != other.status)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AccountUserModel [accountUserId=" + accountUserId + ", username=" + username + ", status=" + status
				+ ", account=" + account + ", adminRoleFlag=" + adminRoleFlag + "]";
	}
	
	
	

}
