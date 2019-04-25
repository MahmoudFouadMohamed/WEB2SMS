package com.edafa.web2sms.acc_manag.service.account.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

@XmlType(name = "AccountResult", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/model/")
public class AccountResult extends ResultStatus {

	@XmlElement(name = "Account", required = true, nillable = false)
	AccountModel account;

	public AccountResult() {
	}

	public AccountResult(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	public AccountResult(ResponseStatus status) {
		super(status);
	}

	public AccountResult(ResponseStatus status, AccountModel account) {
		super(status);
		this.account = account;
	}

	public AccountModel getAccount() {
		return account;
	}

	public void setAccount(AccountModel account) {
		this.account = account;
	}
}
