package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import java.io.Serializable;

@XmlType(name = "AccountProvTrxInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccountProvTrxInfo extends AccountTrxInfo implements Serializable {

	@XmlElement(required = true, nillable = false)
	String accountId;

	@XmlTransient
	private ProvRequestTypeName provReqType;

	public AccountProvTrxInfo() {
		// TODO Auto-generated constructor stub
	}

	public AccountProvTrxInfo(String trxId) {
		super(trxId);
	}

	public AccountProvTrxInfo(String trxId, String accountId, ProvRequestTypeName provReqType) {
		super(trxId);
		this.accountId = accountId;
		this.provReqType = provReqType;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setProvReqType(ProvRequestTypeName reqType) {
		this.provReqType = reqType;
	}

	public ProvRequestTypeName getProvReqType() {
		return provReqType;
	}

	@XmlTransient
	public boolean isValid() {
		return (super.isValid() && accountId != null && !accountId.isEmpty());
	}

	@XmlTransient
	@Override
	public String logId() {
		return "ProvTrx" + super.logId();
	}

	@XmlTransient
	public String logInfo() {
		String log = "ProvTrx(" + trxId + ")";
		if (provReqType != null) {
			log += " ProvRequestType(" + provReqType + ")";
		}

		if (accountId != null) {
			log += " AccountId(" + accountId + ")";
		}

		log += ": ";

		return log;
	}
}