package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;

@XmlType(name = "TrxInfo", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class TrxInfo {

	@XmlElement(required = true, nillable = false)
	protected String trxId;

	public TrxInfo() {}

	public TrxInfo(String trxId) {
		this.trxId = trxId;
	}

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	@XmlTransient
	public boolean isValid() {
		return (trxId != null && !trxId.isEmpty());
	}

	@XmlTransient
	public String logId() {
		return "(" + trxId + "): ";
	}

	public String logInfo() {
		return logId();
	}

	public AccountTrxInfo getAccountTrxInfo() {
		return new AccountTrxInfo(this.trxId);
	}

	@Override
	public String toString() {
		return "trxId=" + trxId;
	}
}
