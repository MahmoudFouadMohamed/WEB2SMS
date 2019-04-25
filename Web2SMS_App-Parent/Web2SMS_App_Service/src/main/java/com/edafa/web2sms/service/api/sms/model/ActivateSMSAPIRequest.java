package com.edafa.web2sms.service.api.sms.model;

import java.util.List;

import com.edafa.web2sms.service.model.AdminTrxInfo;

public class ActivateSMSAPIRequest {

	String accountId;
	String password;
	List<String> IPs;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getIPs() {
		return IPs;
	}

	public void setIPs(List<String> iPs) {
		IPs = iPs;
	}

	@Override
	public String toString() {
		return "ActivateSMSAPIRequest [accountId=" + accountId + ", password=" + password + ", list of IP=" + IPs + "]";
	}


}
