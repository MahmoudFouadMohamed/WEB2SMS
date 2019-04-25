package com.edafa.web2sms.acc_manag.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.AccountUser;
import java.io.Serializable;

@XmlType(name = "AccountFullInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountModelFullInfo extends AccountModel implements Serializable {

//	@XmlElement(required = true, nillable = true)
//	private List<ProvisioningRequest> activeProvRequests;

	@XmlElement(name = "sender", required = true, nillable = true)
	private List<String> senders;
	
	@XmlElement(required = true, nillable = true)
	private List<AccountUserModel> accountUsers;

	@XmlElement(name = "intraSender", required = true, nillable = true)
	private List<String> intraSenders;

        @XmlElement(name = "loginUser", required = true, nillable = true)
	private AccManagUserModel loginUser;

	public List<AccountUserModel> getAccountUsers() {
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUserModel> accountUsers) {
		this.accountUsers = accountUsers;
	}

	public List<String> getSenders() {
		return senders;
	}

	public List<String> getIntraSenders() {
		return intraSenders;
	}

	public void setIntraSenders(List<String> intraSenders) {
		this.intraSenders = intraSenders;
	}

	public void setSenders(List<String> senders) {
		this.senders = senders;
	}

    public AccManagUserModel getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(AccManagUserModel loginUser) {
        this.loginUser = loginUser;
    }     
        

	public AccountModelFullInfo() {
	}

	public AccountModelFullInfo(AccountModel account) {
		super(account);
	}

	public AccountModelFullInfo(AccountModelFullInfo account) {
		super(account);
		this.senders = account.getSenders();
		this.intraSenders = account.getIntraSenders();
		this.accountUsers = account.getAccountUsers();
//		 this.activeProvRequests = account.getActiveProvRequests();
	}
}
