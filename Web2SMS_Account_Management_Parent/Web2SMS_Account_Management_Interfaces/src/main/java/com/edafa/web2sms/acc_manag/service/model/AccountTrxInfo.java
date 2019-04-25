package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "TrxInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccountTrxInfo implements Serializable {

	@XmlElement(required = true, nillable = false)
	protected String trxId;

	public AccountTrxInfo() {
	}

	public AccountTrxInfo(String trxId) {
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

	@Override
	public String toString() {
		return "trxId=" + trxId;
	}
}
