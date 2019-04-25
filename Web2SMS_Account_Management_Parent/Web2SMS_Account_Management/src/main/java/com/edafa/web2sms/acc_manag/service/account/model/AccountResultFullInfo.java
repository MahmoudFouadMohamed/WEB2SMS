package com.edafa.web2sms.acc_manag.service.account.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

@XmlType(name = "AccountResultFullInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/model/")
public class AccountResultFullInfo extends ResultStatus {

	@XmlElement(name = "Account", required = true, nillable = false)
	AccountModelFullInfo account;

	public AccountResultFullInfo() {
	}

	public AccountResultFullInfo(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	public AccountResultFullInfo(ResponseStatus status) {
		super(status);
	}

	public AccountResultFullInfo(ResponseStatus status, AccountModelFullInfo account) {
		super(status);
		this.account = account;
	}

	public AccountModel getAccount() {
		return account;
	}

	public void setAccount(AccountModelFullInfo account) {
		this.account = account;
	}
}
